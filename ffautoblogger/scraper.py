#
# Web Scraper to read data from the ESPN Fantasy Football website
#

import requests
from bs4 import BeautifulSoup
from player import Player
from team import Team


class ScraperException(ValueError):
    pass


class Scraper(object):

    def __init__(self, league_id, season_id):
        self.league_id = league_id
        self.season_id = season_id

    def get_teams(self, team_count, week):
        """Gets information from multiple sources and merges them into one data model."""
        teams = self.get_teams_scoreboard(team_count, week)

        for team_id in range(1, team_count + 1):
            players_boxscore = self.get_players_boxscore(team_id, week)
            players_projections = self.get_players_clubhouse(team_id, week)

            for key, player in players_boxscore.items():
                player.merge(players_projections[key])

            teams[team_id].players = players_boxscore
            print('Loaded team #{0}: {1}'.format(team_id, teams[team_id].team_name))

        return teams

    def get_teams_scoreboard(self, team_count, week):
        url = 'http://games.espn.com/ffl/scoreboard'
        args = {'leagueId': self.league_id, 'seasonId': self.season_id, 'matchupPeriodId': week}

        soup = self.__get_soup(url, args)

        teams = [Team(-1)]  # 1-based indexing for teams

        for team_id in range(1, team_count + 1):
            team_row = soup.find('tr', id='teamscrg_{0}_activeteamrow'.format(team_id))
            team = Team(team_id)

            team_name = team_row.find('div', class_='name')
            team.team_name = team_name.find('a').string
            team.team_name_short = team_name.find('span').string

            team_score = team_row.find('td', class_='score')
            team.score = float(team_score.string)

            opp_name = team_row.previous_sibling
            if opp_name is None:
                opp_name = team_row.next_sibling

            opp_id_string = opp_name.attrs['id']
            team.opponent_id = int(opp_id_string.split('_')[1])  # this line assumes a lot :)

            teams.append(team)

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

    def __get_soup(self, url, args):
        response = requests.get(url, params=args)

        if response.status_code != 200:
            raise Exception('Could not reach the espn website! URL={0} params={1}'.format(url, args))

        return BeautifulSoup(response.content, 'html.parser')
