#!/usr/bin/env python3
"""Entry point for the fantasy football auto-blogger"""

from scraper.scraper import Scraper
from blogger.blogger import Blogger
from brain.brain import Brain

# Load the arguments from a config file
# note: the file isn't checked in, create one from the template 'ffautoblogger.ini.template'
args = {}
with open('ffautoblogger.ini', 'r') as config:
    for line in config:
        pair = line.strip().split('=')
        args[pair[0]] = pair[1]

league_id = args['league_id']
season_id = args['season_id']
week_id = args['week_id']

# Scrape all team data from the ESPN website
espn = Scraper(league_id, season_id)

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
