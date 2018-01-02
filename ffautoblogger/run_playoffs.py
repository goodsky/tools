# Run the fantasy football autoblogger
# Special version that merges two weeks of players into a single team

from scraper.scraper import Scraper
from blogger.blogger import Blogger
from brain.brain import Brain

# Load the arguments from a config file
# note: the file isn't checked in, create one from the template 'ffautoblogger.ini.template'
args = {}
config = open('ffautoblogger.ini', 'r')
for line in config:
    pair = line.strip().split('=')
    args[pair[0]] = pair[1]

# Week is the first of the two weeks
clubhouse_id = args['clubhouse_id']
week_id = args['week_id']
team_count = int(args['team_count'])

# Scrape all team data from the ESPN website
espn = Scraper(args['league_id'], args['season_id'])
all_teams = espn.get_teams_playoffs(team_count, clubhouse_id, week_id)

# Create the blogging helper to write HTML
blogger = Blogger()
blogger.blank()
blogger.blank()

# Use a brain to write interesting blogs
brain = Brain(all_teams, blogger)
brain.blog_star_players(count=5)
brain.blog_bust_players(count=5)
brain.blog_bench_star_players(count=5)
brain.blogger.horizontal_line()
brain.blog_match_summaries(count=5)

# Write the formatted blog entry for the week
blogger.write_file(r'C:\Users\Skyler\Downloads\week_{0}.html'.format(week_id))
