#!/bin/usr/env python3

import requests
from bs4 import BeautifulSoup

from scraper.player import Player
from scraper.team import Team


class ESPNScraper(object):
    """ Tools to load Fantasy Football league, team and player data from the ESPN fantasy football public website. """

    def __init__(self, league_id, season_id):
        """ Create a scraper for the given league, season and week"""
        self.league_id = league_id
        self.season_id = season_id

    def get_teams(self, week):
        """Gets team data from multiple sources and merges them into one data model."""
        teams = self.get_teams_scoreboard(week)

        for team in teams.values():
            players_boxscore = self.get_players_boxscore(team.team_id, week)
            players_projections = self.get_players_clubhouse(team.team_id, week)

            for key, player in players_boxscore.items():
                player.merge(players_projections[key])

            team.players = players_boxscore
            print('Loaded team #{0}: {1}'.format(team.team_id, team.team_name))

        return teams
    
    def get_teams_playoffs(self, matchup_id, week):
        """Gets information from multiple sources and merges them into one data model."""
        week2 = str(int(week) + 1)
        teams = self.get_teams_scoreboard(matchup_id)
        
        for team in teams.values():
            players_boxscore1 = self.get_players_boxscore(team.team_id, week)
            players_projections1 = self.get_players_clubhouse(team.team_id, week)
            
            players_boxscore2 = self.get_players_boxscore(team.team_id, week2)
            players_projections2 = self.get_players_clubhouse(team.team_id, week2)
            
            all_players = {}
            
            for key, player in players_boxscore1.items():
                player.merge(players_projections1[key])
                player.name = "(1) " + player.name
                player.player_id = "1-" + player.player_id
                all_players[player.player_id] = player
            
            for key, player in players_boxscore2.items():
                player.merge(players_projections2[key])
                player.name = "(2) " + player.name
                player.player_id = "2-" + player.player_id
                all_players[player.player_id] = player
            
            team.players = all_players
            print('Loaded team #{0}: {1} ({2} players)'.format(team.team_id, team.team_name, len(all_players)))

        return teams

    def get_teams_scoreboard(self, week):
        """Loads the scoreboard page with the current score and matchups of each team
        Returns: Dictionary of Teams indexed by team_id with data that is scraped from the scoreboard.
        """
        url = 'http://games.espn.com/ffl/scoreboard'
        args = { 'leagueId': self.league_id, 'seasonId': self.season_id, 'matchupPeriodId': week }

        scoreboard = get_webpage(url, args)
        team_rows = scoreboard.find_all('td', class_='team')

        teams = {}
        for team_row in team_rows:
            name_row = team_row.find('div', class_='name')
            name_link = name_row.find('a')
            name_span = name_row.find('span')
            record_span = team_row.find('span', class_='record')
            owners_span = team_row.find('span', class_='owners')
            score_row = team_row.parent.find('td', class_='score')

            team_id = int(team_row.parent.attrs['id'].split('_')[1])
            team_name = name_link.string
            team_name_short = name_span.string[1:-1]            # remove the '(' ')'
            team_record = record_span.string[1:-1].split('-')   # remove the '(' ')' and split into wins-losses
            coach_name = owners_span.string.split(' ')[0]       # take only the first name
            team_score = float(score_row.string)

            team = Team(team_id)
            team.team_name = team_name
            team.team_name_short = team_name_short
            team.coach_name = coach_name
            team.team_wins = int(team_record[0])
            team.team_losses = int(team_record[1])
            team.score = team_score

            opp_name = team_row.parent.previous_sibling
            if opp_name is None:
                opp_name = team_row.parent.next_sibling

            opp_id_string = opp_name.attrs['id']
            team.opponent_id = int(opp_id_string.split('_')[1])

            teams[team.team_id] = team

        return teams

    def get_players_boxscore(self, team_id, week):
        """Loads the boxscore page with details about the performance of each player.
        Returns: List of players for the requested team and week with current performance data."""
        url = "http://games.espn.com/ffl/boxscorefull"
        args = {'leagueId': self.league_id, 'teamId': team_id, 'seasonId': self.season_id, 'scoringPeriodId': week}

        soup = get_webpage(url, args)

        players = {}

        table_index = -1
        while True:
            table_index += 1

            table = soup.find('table', id='playertable_{0}'.format(table_index))
            if table_index != 0 and table.find('tr', class_='playerTableBgRowHead') is not None:
                # We ran into the opponent's table
                break

            table_type = table.find('tr', class_='playerTableBgRowSubhead')\
                .find('th', class_='playertableSectionHeaderFirst').string
            if 'OFFENSIVE PLAYERS' in table_type:
                rows = table.find_all('tr', class_='pncPlayerRow')
                for row in rows:
                    player_boxscore = Player(team_id)
                    player_boxscore.parse_boxscore_offense(row)
                    players[player_boxscore.player_id] = player_boxscore
            elif 'KICKERS' in table_type:
                rows = table.find_all('tr', class_='pncPlayerRow')
                for row in rows:
                    player_boxscore = Player(team_id)
                    player_boxscore.parse_boxscore_kicker(row)
                    players[player_boxscore.player_id] = player_boxscore
            elif 'TEAM D/ST' in table_type:
                rows = table.find_all('tr', class_='pncPlayerRow')
                for row in rows:
                    player_boxscore = Player(team_id)
                    player_boxscore.parse_boxscore_defense(row)
                    players[player_boxscore.player_id] = player_boxscore
            else:
                print("Unknown Table Type!!!")

        return players

    def get_players_clubhouse(self, team_id, week):
        """Loads the clubhouse data for a player. This includes the projections and historic performance of players.
        Returns. List of players for the requested team and week with projection data."""
        url = 'http://games.espn.com/ffl/clubhouse'
        args = {'leagueId': self.league_id, 'teamId': team_id, 'seasonId': self.season_id, 'scoringPeriodId': week}

        soup = get_webpage(url, args)
        table = soup.find('table', class_='playerTableTable')

        players = {}
        rows = table.find_all('tr', class_='pncPlayerRow')
        for row in rows:
            player_projection = Player(team_id)
            player_projection.parse_clubhouse(row)
            players[player_projection.player_id] = player_projection

        return players

def get_webpage(url, args):
    """Make a request to the webpage and return the BeautifulSoup parsed result. 
    Raises: ConnectionError
    """
    try:
        response = requests.get(url, params=args)
    except requests.exceptions.ConnectionError:
        raise ConnectionError('Failed to GET the webpage! URL={0} params={1}'.format(url, args))

    if (response.status_code != 200):
        raise ConnectionError('Page returned non-success status code. Status: {0} URL={1} params={2}'.format(response.status_code, url, args))

    return BeautifulSoup(response.content, 'html.parser')
