#!/usr/bin/env python3

# Add the Test directories into the search path
import sys, os
current_dir = os.path.dirname(__file__)
sys.path.insert(0, os.path.join(current_dir, '..'))

import unittest
from autoblogger import load_configuration
from scraper.espn_scraper import ESPNScraper, get_webpage


class ESPNScraperTests(unittest.TestCase):
    """ Tests for the ''scraper'' class """

    def setUp(self):
        args = load_configuration()
        self.league_id = args['league_id']
        self.season_id = args['season_id']
        self.week_id = args['week_id']
        self.espn = ESPNScraper(self.league_id, self.season_id)

    def test_invalid_webpage(self):
        with self.assertRaises(ConnectionError):
            get_webpage("http://doesnotexist-failfail.com", { 'foo': 'bar' })
    
    def test_end_to_end(self):
        all_active = {}
        all_bench = {}
        all_teams = self.espn.get_teams(self.week_id)

        print('-------------------------')

        for team in all_teams.values():
            print('TEAM {0} {1} - {2} pts'.format(team.team_name, team.team_name_short, team.score))

            active = [player for player in team.players.values() if player.slot != 'Bench']
            bench = [player for player in team.players.values() if player.slot == 'Bench']

            active_score = 0.0
            for player in active:
                print('\t{0}\t| {1} {2}: {3}/{4} pts'.format(player.slot,
                                                            player.name,
                                                            team.team_name_short,
                                                            player.points,
                                                            player.projected_points))
                active_score += player.points
                all_active[player.player_id] = player

            print('\t\tTotal: {0}'.format(active_score))
            print()

            bench_score = 0.0
            for player in bench:
                print('\t{0} | {1} {2}: {3}/{4} pts'.format(player.slot,
                                                            player.name,
                                                            team.team_name_short,
                                                            player.points,
                                                            player.projected_points))
                bench_score += player.points
                all_bench[player.player_id] = player

            print('\t\tTotal: {0}'.format(bench_score))
            print()

if __name__ == "__main__":
    unittest.main()
