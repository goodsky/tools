#
# Test the blogger
#

from blogger.blogger import Blogger


def run():
    blogger = Blogger()

    for i in range(4):
        blogger.heading('Ministry of Silly Walks vs Commissioner Goodell', underline=True)
        blogger.subheading('FINAL SCORE: foo - bar')
        blogger.minorheading('WINNER: Ministry of Silly Walks (of course)')
        blogger.blank()
        blogger.write('Star Players:', bold=True)
        blogger.write('Kareem Hunt, KC RB (MoSW): 45.6 pts from 148 yds rushing; 98 yds receiving; 1 TD')
        blogger.write('Jordy Nelson, GB WR (MoSW): 20.9 pts from 7 receptions; 1 TD')
        blogger.write('Steelers D/ST (GOOD): 19 pts from 1 TD; 1 INT; 1 Blocked Kick; 7 Sacks')
        blogger.blank()
        blogger.write('Underachievers:', bold=True)
        blogger.write('Jimmy Graham, Sea TE (GOOD): 3.8 pts from 3 receptions')
        blogger.write('Greg Olsen, Car TE (MoSW): 3.8 pts from 2 receptions')
        blogger.blank()
        blogger.blank()

    blogger.write_file(r'D:\blog.html')
