namespace SafariCommandLine.Test
{
    class Program
    {
        static void Main(string[] args)
        {
            CommandLine cli = CommandLine.Instance;
            //cli.TestCharNumGeneration();
            //cli.TestNumberParsing();

            cli.Run();
        }
    }
}
