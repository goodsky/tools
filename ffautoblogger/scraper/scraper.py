#
# Web Scraper to read data from the ESPN Fantasy Football website
#

import requests
from bs4 import BeautifulSoup
from urllib.parse import urlparse, parse_qs

from scraper.player import Player
from scraper.team import Team


class ScraperException(ValueError):
    pass


class Scraper(object):

    def __init__(self, league_id, season_id):
        self.league_id = league_id
        self.season_id = season_id

    def get_teams(self, week):
        """Gets information from multiple sources and merges them into one data model."""
        teams = self.get_teams_scoreboard(week)

        for team in teams.values():
            players_boxscore = self.get_players_boxscore(team.team_id, week)
            players_projections = self.get_players_clubhouse(team.team_id, week)

            for key, player in players_boxscore.items():
                player.merge(players_projections[key])

            team.players = players_boxscore
            print('Loaded team #{0}: {1}'.format(team.team_id, team.team_name))

        return teams
    
    def get_teams_playoffs(self, clubhouse_week, week):
        """Gets information from multiple sources and merges them into one data model."""
        week2 = str(int(week) + 1)
        teams = self.get_teams_scoreboard(clubhouse_week)
        
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
        url = 'http://games.espn.com/ffl/scoreboard'
        args = {'leagueId': self.league_id, 'seasonId': self.season_id, 'matchupPeriodId': week}

        scoreboard = self.__get_soup(url, args)
        team_rows = scoreboard.find_all('td', class_='team')

        teams = {}
        for team_row in team_rows:
            name_row = team_row.find('div', class_='name')
            name_link = name_row.find('a')
            name_span = name_row.find('span')
            score_row = team_row.parent.find('td', class_='score')

            clubhouse_link = name_link.get('href')
            team_id = self.__get_team_id(clubhouse_link)
            team_name = name_link.string
            team_name_short = name_span.string
            team_score = float(score_row.string)

            team = Team(team_id)
            team.team_name = team_name
            team.team_name_short = team_name_short
            team.score = team_score

            opp_name = team_row.parent.previous_sibling
            if opp_name is None:
                opp_name = team_row.parent.next_sibling

            opp_id_string = opp_name.attrs['id']
            team.opponent_id = int(opp_id_string.split('_')[1])     # this line assumes a lot :)
                                                                    # but maybe this is an okay way to get the team id?

            teams[team.team_id] = team

        return teams

    def get_players_boxscore(self, team_id, week):
        url = "http://games.espn.com/ffl/boxscorefull"
        args = {'leagueId': self.league_id, 'teamId': team_id, 'seasonId': self.season_id, 'scoringPeriodId': week}

        soup = self.__get_soup(url, args)

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

        return players

    def get_players_clubhouse(self, team_id, week):
        url = 'http://games.espn.com/ffl/clubhouse'
        args = {'leagueId': self.league_id, 'teamId': team_id, 'seasonId': self.season_id, 'scoringPeriodId': week}

        soup = self.__get_soup(url, args)
        table = soup.find('table', class_='playerTableTable')

        players = {}

        rows = table.find_all('tr', class_='pncPlayerRow')
        for row in rows:
            player_projection = Player(team_id)
            player_projection.parse_clubhouse(row)
            players[player_projection.player_id] = player_projection

        return players

    def __get_team_id(self, url):
        parsed_url = urlparse(url)
        query_strings = parse_qs(parsed_url.query)
        return int(query_strings['teamId'][0])

    def __get_soup(self, url, args):
        response = requests.get(url, params=args)

        if response.status_code != 200:
            raise Exception('Could not reach the espn website! URL={0} params={1}'.format(url, args))

        return BeautifulSoup(response.content, 'html.parser')
