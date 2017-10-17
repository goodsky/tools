# Run the fantasy football autoblogger

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

week_id = args['week_id']
team_count = int(args['team_count'])

# Scrape all team data from the ESPN website
espn = Scraper(args['league_id'], args['season_id'])
all_teams = espn.get_teams(team_count, week_id)

# Create the blogging helper to write HTML
blogger = Blogger()
blogger.write(' ')

# Use a brain to write interesting blogs
brain = Brain(all_teams, blogger)
brain.blog_star_players()
brain.blog_bust_players()
brain.blog_bench_star_players(count=3)
brain.blogger.horizontal_line()
brain.blog_match_summaries()

# Write the formatted blog entry for the week
blogger.write_file(r'D:\week_{0}.html'.format(week_id))
