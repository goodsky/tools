#!/usr/bin/env python3

import os

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
        matchup_id = args['matchup_id']
        all_teams = espn.get_teams_playoffs(matchup_id, week_id)
    else:
        all_teams = espn.get_teams(week_id)

    # Create the blogging helper to write HTML
    blogger = Blogger()
    blogger.blank(count=3)

    # Use a brain to write interesting blogs
    brain = Brain(week_id, all_teams, blogger)

    brain.blog_current_standings()
    blogger.blank(count=3)

    brain.blogger.horizontal_line()
    brain.blogger.heading("League Wide Winners and Losers", underline=True, center=True)
    brain.blog_star_players(count=5)
    brain.blog_bust_players(count=5)
    brain.blog_bench_star_players(count=5)

    brain.blogger.horizontal_line()
    brain.blog_match_summaries()

    next_week_id = str(int(week_id) + 1)
    next_week_teams = espn.get_teams_scoreboard(next_week_id)
    brain.blog_coming_up_next(next_week_id, next_week_teams)

    # Write the formatted blog entry for the week
    output_file = os.path.expandvars('%UserProfile%\\Documents\\{0}_week_{1}.html'.format(season_id, week_id))
    blogger.write_file(output_file)

def load_configuration(file='ffautoblogger.ini'):
    """ Load the program configuration ini file. """
    args = {}
    with open(file, 'r') as config:
        for line in config:
            pair = line.strip().split('=')
            args[pair[0]] = pair[1]
    
    return args

if __name__ == "__main__":
    main()