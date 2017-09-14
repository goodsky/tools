#
# Web Scraper to read data from the ESPN Fantasy Football website
#

import requests
from bs4 import BeautifulSoup
from player import Player


class ScraperException(ValueError):
    pass


class Scraper(object):

    def __init__(self, league_id, season_id):
        self.league_id = league_id
        self.season_id = season_id

    def get_clubhouse(self, team_id, week):
        url = 'http://games.espn.com/ffl/clubhouse'
        args = {'leagueId': self.league_id, 'teamId': team_id, 'seasonId': self.season_id, 'scoringPeriodId': week}

        soup = self.__get_soup(url, args)
        table = soup.find('table', class_='playerTableTable')

        players = {}

        rows = table.find_all('tr', class_='pncPlayerRow')
        for row in rows:
            player_projection = Player(team_id)
            player_projection.parse_clubhouse(row)
            players[player_projection.id] = player_projection

        return players

    def get_boxscore(self, team_id, week):
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

            table_type = table.find('tr', class_='playerTableBgRowSubhead').find('th', class_='playertableSectionHeaderFirst').string
            if 'OFFENSIVE PLAYERS' in table_type:
                rows = table.find_all('tr', class_='pncPlayerRow')
                for row in rows:
                    player_boxscore = Player(team_id)
                    player_boxscore.parse_boxscore_offense(row)
                    players[player_boxscore.id] = player_boxscore
            elif 'KICKERS' in table_type:
                rows = table.find_all('tr', class_='pncPlayerRow')
                for row in rows:
                    player_boxscore = Player(team_id)
                    player_boxscore.parse_boxscore_kicker(row)
                    players[player_boxscore.id] = player_boxscore
            elif 'TEAM D/ST' in table_type:
                rows = table.find_all('tr', class_='pncPlayerRow')
                for row in rows:
                    player_boxscore = Player(team_id)
                    player_boxscore.parse_boxscore_defense(row)
                    players[player_boxscore.id] = player_boxscore

        return players

    def __get_soup(self, url, args):
        response = requests.get(url, params=args)

        if response.status_code != 200:
            raise Exception('Could not reach the espn website! URL={0} params={1}'.format(url, args))

        return BeautifulSoup(response.content, 'html.parser')
