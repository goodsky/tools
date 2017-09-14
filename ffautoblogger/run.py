# Run the fantasy football autoblogger

from scraper import Scraper

args = {}

config = open('ffautoblogger.ini', 'r')
for line in config:
    pair = line.strip().split('=')
    args[pair[0]] = pair[1]

espn = Scraper(args['league_id'], args['season_id'])

all_players = {}

week_id = args['week_id']
for team_id in range(1, int(args['team_count']) + 1):
    players_projections = espn.get_clubhouse(team_id, week_id)
    players_boxscore = espn.get_boxscore(team_id, week_id)

    for key, player in players_boxscore.items():
        all_players[key] = player

for key, player in all_players.items():
    print('{0}, {1} ({2}): {3} pts'.format(player.name, player.slot, player.team_id, player.points))
