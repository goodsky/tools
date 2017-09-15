#
# My brain gets tired sometimes. Let's make a new one.
#


class Brain(object):
    def __init__(self, teams, blogger):
        self.teams = teams
        self.blogger = blogger

        self.players = []
        for team in teams[1:]:
            for player in team.players.values():
                self.players.append(player)

        self.active_players = [player for player in self.players if player.slot != 'Bench']
        self.bench_players = [player for player in self.players if player.slot == 'Bench']

    def blog_match_summaries(self):
        """
        Write the blog headings for each match's summary.
        Indicate the final score as well as the Star Players and Underachievers.
        """

        top_teams = list(self.teams[1:])
        top_teams.sort(key=lambda t: t.score, reverse=True)

        seen_match = set()
        for team in top_teams:
            if team.team_id in seen_match:
                continue

            opp_team = self.teams[team.opponent_id]
            seen_match.add(team.team_id)
            seen_match.add(opp_team.team_id)

            match_players = []
            for player in team.players.values():
                if player.slot != 'Bench':
                    match_players.append(player)
            for player in opp_team.players.values():
                if player.slot != 'Bench':
                    match_players.append(player)

            match_players.sort(key=lambda p: p.points, reverse=True)

            self.blogger.heading(team.team_name + ' vs ' + opp_team.team_name, underline=True)
            self.blogger.subheading('FINAL SCORE: {0} - {1}'.format(team.score, opp_team.score))
            self.blogger.minorheading('WINNER: {0}'.format(team.team_name if team.score > opp_team.score else opp_team.team_name))
            self.blogger.blank()

            # Star Players are picked by overall top score
            self.blogger.write('Star Players:', bold=True)
            for player in match_players[:3]:
                self.blogger.write('{0}, {1} {2} {3}: {4:.2f} pts from {5}'\
                                   .format(player.name,
                                           player.team if player.team is not None else '',
                                           player.slot,
                                           self.teams[player.team_id].team_name_short,
                                           player.points,
                                           self.__interesting_stats(player)))
            self.blogger.blank()

            # Underachievers are picked by performance vs expectation
            match_players.sort(key=lambda p: p.points - p.projected_points)
            self.blogger.write('Underachievers:', bold=True)
            for player in match_players[:3]:
                self.blogger.write('{0}, {1} {2} {3}: {4:.2f} pts ({5:.2f} projected) from {6}'\
                                   .format(player.name,
                                           player.team if player.team is not None else '',
                                           player.slot,
                                           self.teams[player.team_id].team_name_short,
                                           player.points,
                                           player.points - player.projected_points,
                                           self.__interesting_stats(player)))
            self.blogger.blank()

            self.blogger.blank()
            self.blogger.blank()
            self.blogger.blank()

    def blog_star_players(self, count=3):
        """
        Write the blog headings for the league-wide star players.
        """

        self.active_players.sort(key=lambda p: p.points - p.projected_points, reverse=True)

        self.blogger.heading('League All-Star Players', underline=True)
        for i in range(count):
            player = self.active_players[i]
            self.blogger.write('{0}, {1} {2} {3}: {4:.2f} pts (+{5:.2f} over projected)'\
                               .format( player.name,
                                        player.team if player.team is not None else '',
                                        player.slot,
                                        self.teams[player.team_id].team_name_short,
                                        player.points,
                                        player.points - player.projected_points))
        self.blogger.blank()

    def blog_bust_players(self, count=3):
        """
        Write the blog headings for the league-wide busts.
        """

        self.active_players.sort(key=lambda p: p.points - p.projected_points)

        self.blogger.heading('League Bust Players', underline=True)
        for i in range(count):
            player = self.active_players[i]
            self.blogger.write('{0}, {1} {2} {3}: {4:.2f} pts (-{5:.2f} under projected)'\
                               .format( player.name,
                                        player.team if player.team is not None else '',
                                        player.slot,
                                        self.teams[player.team_id].team_name_short,
                                        player.points,
                                        player.projected_points - player.points))
        self.blogger.blank()

    def __interesting_stats(self, player):
        """
        Compose an interesting string about this player's stats.
        Plenty of room for improvement here. Go hog wild!
        """

        if player.slot == 'QB':
            return '{0} yds passing; {1} TD; {2} INT'\
                .format(player.pass_yds, player.pass_tds + player.rush_tds, player.pass_ints)
        elif player.slot == 'RB':
            return '{0} yds; {1} TD; {2} RUNS'\
                .format(player.rush_yds + player.rec_yds, player.rush_tds + player.rec_tds, player.rush_attempts)
        elif player.slot == 'WR':
            return '{0} yds; {1} TD; {2}/{3} REC'\
                .format(player.rush_yds + player.rec_yds, player.rush_tds + player.rec_tds, player.rec_caught, player.rec_targets)
        elif player.slot == 'TE':
            return '{0} yds; {1} TD; {2}/{3} REC'\
                .format(player.rush_yds + player.rec_yds, player.rush_tds + player.rec_tds, player.rec_caught, player.rec_targets)
        elif player.slot == 'FLEX':
            return '{0} yds; {1} TD; {2}/{3} REC'\
                .format(player.rush_yds + player.rec_yds, player.rush_tds + player.rec_tds, player.rec_caught, player.rec_targets)
        elif player.slot == 'K':
            return '{0} field goals; {1} XP'\
                .format(player.kick_total, player.kick_xp)
        elif player.slot == 'D/ST':
            stats_max_length = 50
            stats = ''
            if player.defense_tds != 0 and len(stats) < stats_max_length:
                stats += '{0} TD; '.format(player.defense_tds)
            if player.defense_ints != 0 and len(stats) < stats_max_length:
                stats += '{0} INT; '.format(player.defense_ints)
            if player.defense_fumbles != 0 and len(stats) < stats_max_length:
                stats += '{0} FR; '.format(player.defense_fumbles)
            if player.defense_sacks != 0 and len(stats) < stats_max_length:
                stats += '{0} SCK; '.format(player.defense_sacks)
            if player.defense_safety != 0 and len(stats) < stats_max_length:
                stats += '{0} SFTY; '.format(player.defense_safety)
            if player.defense_blocked != 0 and len(stats) < stats_max_length:
                stats += '{0} BLK; '.format(player.defense_blocked)

            return '{0} PA; {1}'.format(player.defense_pts_against, stats)
