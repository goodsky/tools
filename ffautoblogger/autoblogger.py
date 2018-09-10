#!/usr/bin/env python3
"""Entry point for the fantasy football auto-blogger"""

from scraper.espn_scraper import ESPNScraper
from blogger.blogger import Blogger
from brain.brain import Brain

def main():
    """ Main entry for the auto-blogger program """

    args = load_configuration()
    league_id = args['league_id']
    season_id = args['season_id']
    week_id = args['week_id']

    # Scrape all team data from the ESPN website
    espn = ESPNScraper(league_id, season_id)
    
    if 'is_playoffs' in args:
        clubhouse_id = args['clubhouse_id']
        all_teams = espn.get_teams_playoffs(clubhouse_id, week_id)
    else:
        all_teams = espn.get_teams(week_id)

    # Create the blogging helper to write HTML
    blogger = Blogger()
    blogger.blank()
    blogger.blank()

    # Use a brain to write interesting blogs
    brain = Brain(all_teams, blogger)
    brain.blog_star_players()
    brain.blog_bust_players()
    brain.blog_bench_star_players(count=3)
    brain.blogger.horizontal_line()
    brain.blog_match_summaries()

    # Write the formatted blog entry for the week
    blogger.write_file(r'C:\Users\Skyler\Downloads\week_{0}.html'.format(week_id))

def load_configuration(file='ffautoblogger.ini'):
    """ Load the program configuration. """

    args = {}
    with open(file, 'r') as config:
        for line in config:
            pair = line.strip().split('=')
            args[pair[0]] = pair[1]
    
    return args

if __name__ == "__main__":
    main()