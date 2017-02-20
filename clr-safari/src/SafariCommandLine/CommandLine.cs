using SafariCommandLine.Attributes;

using System;
using System.Reflection;

namespace SafariCommandLine
{
    /// <summary>
    // Runner that will find classes marked with the <cref="RunnableAttribute" /> and list them to be run.
    /// </summary>
    public sealed class CommandLine : IDisposable
    {
        private string _assembly;
        private RunnableCollection _runnables;

        /// <summary>
        /// Private ctor to force Singleton pattern
        /// </summary>
        private CommandLine(Assembly assembly)
        { 
            _runnables = new RunnableCollection();

            _assembly = assembly.FullName;
            foreach (var type in assembly.GetTypes())
            {
                var runnableAttribute = default(RunnableAttribute);
                if ((runnableAttribute = type.GetCustomAttribute<RunnableAttribute>()) != null)
                {
                    // NB: Require a default constructor for all runnables
                    var runnable = Activator.CreateInstance(type) as IRunnable;

                    if (runnable != null)
                        _runnables.AddRunnable(runnable, runnableAttribute.Name, runnableAttribute.Category);
                }
            }
        }

        public void Run()
        {
            Console.ForegroundColor = ConsoleColor.Yellow;
            Console.WriteLine(@"   Welcome to Skyler's                                "); // 55 char width
            Console.WriteLine(@"------------------------------------------------------");
            Console.WriteLine(@"   _____ _      _____     _____        __           _ ");
            Console.WriteLine(@"  / ____| |    |  __ \   / ____|      / _|         (_)");
            Console.WriteLine(@" | |    | |    | |__) | | (___   __ _| |_ __ _ _ __ _ ");
            Console.WriteLine(@" | |    | |    |  _  /   \___ \ / _` |  _/ _` | '__| |");
            Console.WriteLine(@" | |____| |____| | \ \   ____) | (_| | || (_| | |  | |");
            Console.WriteLine(@"  \_____|______|_|  \_\ |_____/ \__,_|_| \__,_|_|  |_|");
            Console.WriteLine(@"                                                      ");
            Console.WriteLine(@"------------------------------------------------------");

            Console.ForegroundColor = ConsoleColor.Cyan;
            Console.WriteLine(@"                                                      ");
            Console.WriteLine(@"Loaded {0} classes from assembly {1}                  ", _runnables.Count, _assembly);
            Console.ResetColor();

            for (bool running = true; running;)
            {
                PrintMenu();

                var command = Console.ReadLine().ToLower();

                switch (command)
                {
                    case "quit":
                    case "exit":
                        running = false;
                        break;
                    default:
                        int id1, id2;
                        if (TryParseNumber(command, out id1, out id2))
                        {
                            var runnable = _runnables.GetEntryById(id1, id2);

                            if (runnable == null)
                            {
                                Console.WriteLine("Command {0} ({1}, {2}) does not exist in this list. Try again.", command, id1, id2);
                                break;
                            }

                            Console.WriteLine("Executing {0}.", runnable.Name);

                            try
                            {
                                runnable.Class.Execute();
                            }
                            catch (Exception e)
                            {
                                Console.ForegroundColor = ConsoleColor.Red;
                                Console.WriteLine("--- ! --- ! --- ! --- ! --- ! ---");
                                Console.WriteLine("Exception while executing {0}.", runnable.Name);
                                Console.WriteLine(e);
                            }
                            finally
                            {
                                Console.ResetColor();
                            }
                        }
                        else
                        {
                            Console.WriteLine("Unknown command. Type 'exit' or 'quit' to close the application.");
                        }
                        break;
                }

                Console.WriteLine();
            }

            Console.ForegroundColor = ConsoleColor.Green;
            Console.WriteLine("Goodbye.");
            Console.ResetColor();
        }

        public void PrintMenu()
        {
            int categoryId = 0;
            foreach (var category in _runnables.Categories)
            {
                Console.ForegroundColor = ConsoleColor.Yellow;
                Console.WriteLine(@"Category: {0}", category);
                Console.ForegroundColor = ConsoleColor.White;

                int entryId = 0;
                foreach (var runnable in _runnables[category])
                {
                    Console.WriteLine("    {0}{1}) {2}", categoryId, ToCharNum(entryId), runnable.Name);
                    entryId++;
                }

                categoryId++;
                Console.WriteLine();
            }

            Console.ResetColor();
        }

        public void Dispose()
        {
            _runnables.Dispose();
        }

        #region Singleton
        /// <summary>
        /// Lock to initialize singleton CLI
        /// </summary>
        private static object _instanceLock = new object();

        /// <summary>
        /// Singleton CLI instance
        /// </summary>
        private static CommandLine _instance;

        /// <summary>
        /// Singleton CLI instance accessor
        /// </summary>
        public static CommandLine Instance
        {
            get
            {
                if (_instance == null)
                {
                    lock (_instanceLock)
                    {
                        if (_instance == null)
                        {
                            _instance = new CommandLine(Assembly.GetCallingAssembly());
                        }
                    }
                }

                return _instance;
            }
        }
        #endregion

        /// <summary>
        /// Turn an integer into a base 26 style number where a-z represents the numbers
        /// </summary>
        /// <param name="id"></param>
        /// <returns></returns>
        private string ToCharNum(int id)
        {
            string s = string.Empty;
            for (;;)
            {
                s = (char)('a' + (id % 26)) + s;

                id /= 26;
                if (id == 0)
                    break;
            }

            return s;
        }

        /// <summary>
        /// Parsing the command line option from the form of 1234abcd where 1234 is the first index, then abcd is the next.
        /// </summary>
        /// <param name="input">The input string</param>
        /// <param name="id1">Output parameter. Value of the digit portion of the input.</param>
        /// <param name="id2">Output parameter. Value of the char portion of the input (base 26).</param>
        /// <returns>True if the parsing succeeds. False otherwise.</returns>
        private bool TryParseNumber(string input, out int id1, out int id2)
        {
            id1 = -1;
            id2 = -1;

            if (input.Length < 2 || !IsNumber(input[0]) || !IsChar(input[input.Length - 1]))
                return false;

            bool isNums = true;
            int splitIndex = -1;
            for (int i = 0; i < input.Length; ++i)
            {
                if (isNums)
                {
                    if (IsChar(input[i]))
                    {
                        isNums = false;
                        splitIndex = i; // found the split at the first change from num to char
                    }
                    else if (!IsNumber(input[i]))
                    {
                        return false;
                    }
                }
                else
                {
                    if (!IsChar(input[i]))
                    {
                        return false;
                    }
                }
            }

            if (isNums)
                return false; // we need a number and then some characters

            id1 = int.Parse(input.Substring(0, splitIndex));
            id2 = FromCharNum(input.Substring(splitIndex));

            return true;
        }

        private bool IsNumber(char c)
        {
            return c >= '0' && c <= '9';
        }

        private bool IsChar(char c)
        {
            return c >= 'a' && c <= 'z';
        }

        private int FromCharNum(string num)
        {
            num = num.ToLower();

            int id = 0;
            for (int i = 0; i < num.Length; i++)
                id += (num[num.Length - 1 - i] - 'a') * (int)Math.Pow(26, i);

            return id;
        }

        #region Test Methods
        internal void TestCharNumGeneration()
        {
            for (int i = 0; i <= 100; i++)
            {
                Console.WriteLine("{0} -> {1}", i, ToCharNum(i));
            }
        }

        internal void TestNumberParsing()
        {
            for (int i = 0; i <= 100; i++)
            {
                for (int j = 0; j < 50; j++)
                {
                    string s = i.ToString() + ToCharNum(j);
                    int id1, id2;
                    var b = TryParseNumber(s, out id1, out id2);

                    Console.WriteLine("({0}, {1}) -> {2} -> ({3}, {4}) [{5}]", i, j, s, id1, id2, b);
                }
            }
        }

        #endregion
    }
}
