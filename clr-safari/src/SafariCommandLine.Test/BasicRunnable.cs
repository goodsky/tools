using SafariCommandLine.Attributes;

using System;

namespace SafariCommandLine.Test
{
    [Runnable("BasicRunnable")]
    public class BasicRunnable : IRunnable
    {
        public void Execute()
        {
            Console.WriteLine("Basic Runnable.");
        }

        public void Dispose()
        {
        }
    }
}
