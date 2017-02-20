using System;

namespace SafariCommandLine.Attributes
{
    /// <summary>
    /// Provides the safari module name and an optional category for the CLI to print out the options.
    /// </summary>
    [AttributeUsage(validOn:AttributeTargets.Class)]
    public sealed class RunnableAttribute : Attribute
    {
        public static readonly string DefaultCategory = "Default";

        public RunnableAttribute(string name, string category = null)
        {
            Name = name;
            Category = category ?? DefaultCategory;
        }

        public string Name { get; private set; }

        public string Category { get; private set; }
    }
}
