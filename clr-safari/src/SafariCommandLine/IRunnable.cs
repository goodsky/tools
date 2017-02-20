using System;

namespace SafariCommandLine
{
    /// <summary>
    /// Interface for a runnable class.
    /// Is read by the CommandLine runner to set up the safari tests.
    /// </summary>
    public interface IRunnable : IDisposable
    {
        void Execute();
    }
}
