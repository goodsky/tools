#
# Writes simple HTML blog snippets
#


class Blogger(object):
    def __init__(self):
        self.__lines = ['<!-- Written by ffautoblogger -->\n']

    def heading(self, line, bold=False, italic=False, underline=False):
        self.__lines.append('<h2>\n' + self.__accent(line, bold, italic, underline) + '\n</h2>\n')

    def subheading(self, line, bold=False, italic=False, underline=False):
        self.__lines.append('<h3>\n' + self.__accent(line, bold, italic, underline) + '\n</h3>\n')

    def minorheading(self, line, bold=False, italic=False, underline=False):
        self.__lines.append('<h4>\n' + self.__accent(line, bold, italic, underline) + '\n</h4>\n')

    def write(self, line, bold=False, italic=False, underline=False):
        self.__lines.append('<div>\n' + self.__accent(line, bold, italic, underline) + '\n</div>\n')

    def blank(self):
        self.__lines.append('<br />\n')

    def horizontal_line(self):
        self.__lines.append('<hr />\n')

    def write_file(self, filename):
        fout = open(filename, 'w')
        fout.writelines(self.__lines)

    def __accent(self, line, bold=False, italic=False, underline=False):
        if bold:
            line = '<b>' + line + '</b>'

        if italic:
            line = '<i>' + line + '</i>'

        if underline:
            line = '<u>' + line + '</u>'

        return line
