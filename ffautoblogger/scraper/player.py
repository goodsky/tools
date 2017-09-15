#
# Class model for a single player with aggregated stats for a given week.
#

player_column = 'PLAYER, TEAM POS'

special_attributes = [
    'name',
    'team',
    'pos',
]

clubhouse_attributes = [
    'slot',                 # SLOT
    player_column,          # Special case for PLAYER, TEAM POS
    None,                   # - blank -
    'opponent',             # OPP
    'status',               # STATUS ET
    None,                   # - blank -
    'rank',                 # PRK
    'total_points',         # PTS
    'average_points',       # AVG
    'last_points',          # LAST
    None,                   # - blank -
    'projected_points',     # PROJ
    'opponent_rank',        # OPRK
    'percent_start',        # %ST
    'percent_own',          # %OWN
    'delta_own',            # +/-
]

boxscore_offense_attributes = [
    'slot',                 # SLOT
    player_column,          # Special case for PLAYER, TEAM POS
    'opponent',             # OPP
    'status',               # STATUS ET
    None,                   # - blank -
    'pass_attempts',        # C/A
    'pass_yds',             # YDS
    'pass_tds',             # TD
    'pass_ints',            # INT
    None,                   # - blank -
    'rush_attempts',        # RUSH
    'rush_yds',             # YDS
    'rush_tds',             # TD
    None,                   # - blank -
    'rec_caught',           # REC
    'rec_yds',              # YDS
    'rec_tds',              # TD
    'rec_targets',          # TAR
    None,                   # - blank -
    'two_point_conv',       # 2PC
    'fumbles',              # FUML
    'misc_tds',              # TD (honestly, I don't know what this one is for)
    None,                   # - blank -
    'points',               # PTS
]

boxscore_kicking_attributes = [
    'slot',                 # SLOT
    player_column,          # Special case for PLAYER, TEAM POS
    'opponent',             # OPP
    'status',               # STATUS ET
    None,                   # - blank -
    'kick_1_39',            # 1-39
    'kick_40-49',           # 40-49
    'kick_50',              # 50+
    'kick_total',           # TOT
    'kick_xp',              # XP
    None,                   # - blank -
    'points',               # PTS
]

boxscore_defense_attributes = [
    'slot',                 # SLOT
    player_column,          # Special case for PLAYER, TEAM POS
    'opponent',             # OPP
    'status',               # STATUS ET
    None,                   # - blank -
    'defense_tds',          # TD
    'defense_ints',         # INT
    'defense_fumbles',      # FR
    'defense_sacks',        # SCK
    'defense_safety',       # SFTY
    'defense_blocked',      # BLK
    'defense_pts_against',  # PA
    None,                   # - blank -
    'points',               # PTS
]


class Player(object):
    """
    Class representing an NFL player
    """

    def __init__(self, team_id):
        self.player_id = 'default'
        self.team_id = team_id

        for value in special_attributes + \
                clubhouse_attributes + \
                boxscore_offense_attributes + \
                boxscore_kicking_attributes + \
                boxscore_defense_attributes:
            if value is None:
                continue
            self.__dict__[value] = None

    def parse_clubhouse(self, row):
        self.parse(row, clubhouse_attributes)

    def parse_boxscore_offense(self, row):
        self.parse(row, boxscore_offense_attributes)

    def parse_boxscore_kicker(self, row):
        self.parse(row, boxscore_kicking_attributes)

    def parse_boxscore_defense(self, row):
        self.parse(row, boxscore_defense_attributes)

    def parse(self, row, column_titles):
        column_index = -1
        for column in row.children:
            column_index += 1
            header = column_titles[column_index]

            if header is None:
                continue

            if header == player_column:
                # Special Case: PLAYER, TEAM POS column is actually three values
                name_parts = list(column.children)

                # if someone left an empty spot in their lineup
                if len(name_parts) == 1:
                    self.player_id = 'empty'
                    continue

                self.player_id = column.attrs['id']
                self.__dict__['name'] = name_parts[0].string

                team_pos = name_parts[1].string.split()
                if len(team_pos) == 1:
                    self.__dict__['team'] = None
                    self.__dict__['pos'] = team_pos[0]
                else:
                    self.__dict__['team'] = team_pos[1]
                    self.__dict__['pos'] = team_pos[2]
            elif header == 'opponent' and column.text == '** BYE **':
                # Special Case: ** BYE ** week makes the table have fewer columns.
                self.__dict__[column_titles[column_index]] = column.text
                column_index += 1
                self.__dict__[column_titles[column_index]] = column.text
            else:
                # Base Case: Attempt to parse a numeric value if you can.
                try:
                    val = column.text
                    if val == '--':
                        val = '0'

                    if '.' in val:
                        self.__dict__[header] = float(val)
                    else:
                        self.__dict__[header] = int(val)

                except ValueError:
                    self.__dict__[header] = column.text

    def merge(self, o):
        for key, value in o.__dict__.items():
            if value is not None and self.__dict__[key] is None:
                self.__dict__[key] = value
