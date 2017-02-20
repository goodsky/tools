using SafariCommandLine.Attributes;

using System;

namespace SafariCommandLine.Test
{
    [Runnable("TestRunnable1", "Test")]
    public class TestRunnable1 : IRunnable
    {
        public void Execute()
        {
            Console.WriteLine("Test Runnable 1.");
        }

        public void Dispose()
        {
        }
    }
}
