
class Team(object):
    """Class representing an NFL team."""

    def __init__(self, team_id):
        self.team_id = team_id
        self.team_name = None
        self.team_name_short = None

        self.team_wins = None
        self.team_losses = None

        self.opponent_id = None
        self.opponent_name = None

        self.players = None
        self.score = 0.0

    def projected_score(self):
        """Calculate the projected score of the team"""
        projected = 0.0
        for player in self.players.values():
            if player.slot != 'Bench':
                projected += player.projected_points

        return projected

    def merge(self, o):
        """Add values from the other team data source into this instance."""
        for key, value in o.__dict__.items():
            if value is not None and self.__dict__[key] is None:
                self.__dict__[key] = value

        for key, player in self.players.items():
            player.merge(o.players[key])
