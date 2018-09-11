
class Brain(object):
    """My brain gets tired sometimes. Let's make a new one."""

    def __init__(self, teams, blogger):
        self.teams = teams
        self.blogger = blogger

        self.players = []
        for team in teams.values():
            for player in team.players.values():
                self.players.append(player)

        self.active_players = [player for player in self.players if player.slot != 'Bench']
        self.bench_players = [player for player in self.players if player.slot == 'Bench']

    def blog_match_summaries(self, count=3):
        """Write the blog headings for each match's summary. Indicate the final score as well as the Star Players and Underachievers."""
        top_teams = list(self.teams.values())
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

            self.blogger.heading(team.team_name + ' vs ' + opp_team.team_name, underline=True, center=True)
            self.blogger.subheading('WINNER: {0}'.format(team.team_name if team.score > opp_team.score else opp_team.team_name), center=True)
            self.blogger.minorheading('PROJECTED SCORE: {0:.1f} - {1:.1f}'.format(team.projected_score(), opp_team.projected_score()), center=True)
            self.blogger.minorheading('FINAL SCORE: {0} - {1}'.format(team.score, opp_team.score), center=True)
            
            self.blogger.blank()

            # Star Players are picked by overall top score
            self.blogger.write('Star Players:', bold=True)
            self.blogger.table_start(border=0)
            for player in match_players[:count]:
                self.blogger.table_row(self.__get_player_summary_columns(player, include_stats=True))
            
            self.blogger.table_end()
            self.blogger.blank()

            # Underachievers are picked by performance vs expectation
            match_players.sort(key=lambda p: p.points - p.projected_points)
            self.blogger.write('Underachievers:', bold=True)
            self.blogger.table_start(border=0)
            for player in match_players[:count]:
                self.blogger.table_row(self.__get_player_summary_columns(player, include_projected=True, include_stats=True))
            
            self.blogger.table_end()
            self.blogger.blank()

            self.blogger.blank()
            self.blogger.blank()
            self.blogger.blank()

    def blog_star_players(self, count=3):
        """Write the blog headings for the league-wide star players."""
        self.active_players.sort(key=lambda p: p.points - p.projected_points, reverse=True)

        self.blogger.subheading('All-Star Players', underline=True, center=True)
        self.blogger.table_start(border=0, center=True)
        for i in range(count):
            player = self.active_players[i]
            self.blogger.table_row(self.__get_player_summary_columns(player, include_projected_verbose=True))

        self.blogger.table_end()
        self.blogger.blank()
        self.blogger.blank()
        self.blogger.blank()

    def blog_bust_players(self, count=3):
        """Write the blog headings for the league-wide busts."""
        self.active_players.sort(key=lambda p: p.points - p.projected_points)

        self.blogger.subheading('Bust Players', underline=True, center=True)
        self.blogger.table_start(border=0, center=True)
        for i in range(count):
            player = self.active_players[i]
            self.blogger.table_row(self.__get_player_summary_columns(player, include_projected_verbose=True))

        self.blogger.table_end()
        self.blogger.blank()
        self.blogger.blank()
        self.blogger.blank()

    def blog_bench_star_players(self, count=1):
        """Write the blog headings for the league-wide bench all-star."""
        self.bench_players.sort(key=lambda p: p.points, reverse=True)

        self.blogger.subheading('Best of the Bench', underline=True, center=True)
        self.blogger.table_start(border=0, center=True)
        for i in range(count):
            player = self.bench_players[i]
            self.blogger.table_row(self.__get_player_summary_columns(player, include_projected_verbose=True))
        
        self.blogger.table_end()
        self.blogger.blank()
        self.blogger.blank()
        self.blogger.blank()

    def blog_coming_up_next(self, next_week_id, next_week_teams):
        """Writes the matchups for next week."""
        top_matchups = list(next_week_teams.values())
        top_matchups.sort(key=lambda t: t.team_wins, reverse=True)

        self.blogger.write("----- Coming up next -----", center=True)
        self.blogger.blank()
        self.blogger.write("Week {0}".format(next_week_id), center=True)
        self.blogger.blank()
        self.blogger.table_start(border=0, center=True)

        seen_matchup = set()
        for team in top_matchups:
            if team.team_id in seen_matchup:
                continue

            opp_team = self.teams[team.opponent_id]
            seen_matchup.add(team.team_id)
            seen_matchup.add(opp_team.team_id)

            team_string1 = '{0} ({1}-{2})'.format(team.team_name, team.team_wins, team.team_losses)
            team_string2 = '{0} ({1}-{2})'.format(opp_team.team_name, opp_team.team_wins, opp_team.team_losses)

            self.blogger.table_row((team_string1, ' vs. ', team_string2))

        self.blogger.table_end()
        self.blogger.blank()
        

    def __get_player_summary(self, player, include_projected=False, include_projected_verbose=False, include_stats=False):
        """Write a single line to summarize a player's performance. Can be parameterized to focus on different areas.
        :param include_projected: Includes a short (+/- X projected)
        :param include_projected_verbose: Includes a longer (+/-X over/under projected)
        :param include_stats: Includes a long string with interesting stats
        """
        summary_columns = self.__get_player_summary_columns(player, include_projected, include_projected_verbose, include_stats)
        return '<b>{1}, {2} {3} {0}</b>: {4}'.format(
                    summary_columns[0], # fantasy team owner
                    summary_columns[1], # player name
                    summary_columns[2], # player position
                    summary_columns[3], # player team (real life)
                    summary_columns[4]) # interesting stats

    def __get_player_summary_columns(self, player, include_projected=False, include_projected_verbose=False, include_stats=False):
        """Retrieve columns about a player's performance. Can be parameterized to focus on different areas.
        :param include_projected: Includes a short (+/- X projected)
        :param include_projected_verbose: Includes a longer (+/-X over/under projected)
        :param include_stats: Includes a long string with interesting stats
        """
        projected_delta = player.points - player.projected_points
        over_projected = projected_delta > 0.0

        projected = ''
        projected_verbose = ''
        stats = ''

        if include_projected:
            projected = '({1}{0:.1f} projected) '.format(
                projected_delta,
                '+' if over_projected else '')

        if include_projected_verbose:
            projected_verbose = '({1}{0:.1f}, {2} projected) '.format(
                projected_delta,
                '+' if over_projected else '',
                'over' if over_projected else 'under')

        if include_stats:
            stats = 'from {0}'.format(self.__get_interesting_stats(player))

        fantasy_team_col = '<b>{0}</b>'.format(self.teams[player.team_id].team_name_short)
        player_name_col = player.name
        player_pos_col = player.slot
        player_team_col = player.team if player.team is not None else 'FA'
        player_stats_col = '{0:.1f} pts {1}{2}{3}'.format(player.points, projected, projected_verbose, stats)

        return (
            fantasy_team_col,
            player_name_col,
            player_pos_col,
            player_team_col,
            player_stats_col
        )

    def __get_interesting_stats(self, player):
        """Compose an interesting string about this player's stats.
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
