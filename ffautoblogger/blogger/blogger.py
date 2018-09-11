
class Blogger(object):
    """Writes simple HTML blog snippets with string formatting."""
    def __init__(self):
        self.__lines = ['<!-- Written by ffautoblogger -->\n']

    def heading(self, line, center=False, bold=False, italic=False, underline=False):
        attributes = ' style="text-align: center"' if center else ''
        self.__lines.append('<h2{}>'.format(attributes) + self.__accent(line, bold, italic, underline) + '</h2>\n')

    def subheading(self, line, center=False, bold=False, italic=False, underline=False):
        attributes = ' style="text-align: center"' if center else ''
        self.__lines.append('<h3{}>'.format(attributes) + self.__accent(line, bold, italic, underline) + '</h3>\n')

    def minorheading(self, line, center=False, bold=False, italic=False, underline=False):
        attributes = ' style="text-align: center"' if center else ''
        self.__lines.append('<h4{}>'.format(attributes) + self.__accent(line, bold, italic, underline) + '</h4>\n')

    def write(self, line, center=False, bold=False, italic=False, underline=False):
        attributes = ' style="text-align: center"' if center else ''
        self.__lines.append('<div{}>'.format(attributes) + self.__accent(line, bold, italic, underline) + '</div>\n')

    def table_start(self, border=1, center=False):
        self.__lines.append('<table border={0} {1}>\n'.format(border, 'align="center"' if center else ''))

    def table_end(self):
        self.__lines.append('</table>\n')

    def table_row(self, columns):
        self.__lines.append('<tr>')
        for column in columns:
            self.__lines.append('<td>{0}</td>'.format(column))
        self.__lines.append('</tr>\n')

    def blank(self):
        self.__lines.append('<br />\n')

    def horizontal_line(self):
        self.__lines.append('<hr />\n')

    def write_file(self, filename):
        with open(filename, 'w') as fout:
            fout.writelines(self.__lines)

    def __accent(self, line, bold=False, italic=False, underline=False):
        if bold:
            line = '<b>' + line + '</b>'

        if italic:
            line = '<i>' + line + '</i>'

        if underline:
            line = '<u>' + line + '</u>'

        return line
