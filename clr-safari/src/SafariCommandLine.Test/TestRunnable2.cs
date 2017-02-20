using SafariCommandLine.Attributes;

using System;

namespace SafariCommandLine.Test
{
    [Runnable("TestRunnable2", "Test")]
    public class TestRunnable2 : IRunnable
    {
        public void Execute()
        {
            Console.WriteLine("Test Runnable 2.");
        }

        public void Dispose()
        {
        }
    }
}
