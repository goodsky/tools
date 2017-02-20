using System;
using System.Collections;
using System.Collections.Generic;

namespace SafariCommandLine
{
    /// <summary>
    /// A record of the runnable classes in the assembly.
    /// </summary>
    internal class RunnableCollection : IEnumerable<RunnableEntry>, IDisposable
    {
        int _count;
        List<string> _categories = new List<string>();
        Dictionary<string, IList<RunnableEntry>> _runnables = new Dictionary<string, IList<RunnableEntry>>();

        public void AddRunnable(IRunnable @class, string name, string category)
        {
            if (@class == null)
                throw new ArgumentException("Class was null.", "class");

            if (string.IsNullOrEmpty(name))
                throw new ArgumentException("Runnable did not have a name.", "name");

            if (string.IsNullOrEmpty(category))
                throw new ArgumentException("Runnable did not have a category.", "category");

            if (!_runnables.ContainsKey(category))
            {
                _categories.Add(category);
                _runnables[category] = new List<RunnableEntry>();
            }

            _runnables[category].Add(new RunnableEntry(@class, name, category));
            ++_count;
        }

        public int Count => _count;

        public IEnumerable<string> Categories
        {
            get
            {
                return _runnables.Keys;
            }
        }

        public IList<RunnableEntry> this[string category]
        {
            get
            {
                return _runnables[category];
            }
        }

        public RunnableEntry GetEntryById(int categoryId, int entryId)
        {
            if (categoryId < 0 || categoryId >= _categories.Count)
                return null;

            var category = _categories[categoryId];

            if (entryId < 0 || entryId >= _runnables[category].Count)
                return null;

            return _runnables[category][entryId];
        }

        #region Implement IEnumerable<>
        public IEnumerator<RunnableEntry> GetEnumerator()
        {
            return new RunnableEnumerator(_runnables.Values);
        }

        IEnumerator IEnumerable.GetEnumerator()
        {
            return new RunnableEnumerator(_runnables.Values);
        }

        private class RunnableEnumerator : IEnumerator<RunnableEntry>
        {
            private int _index;
            private RunnableEntry[] _vals;

            public RunnableEnumerator(IEnumerable<IList<RunnableEntry>> vals)
            {
                _index = 0;

                var list = new List<RunnableEntry>();
                foreach (var val in vals)
                    list.AddRange(val);

                _vals = list.ToArray();
            }

            public bool MoveNext()
            {
                if (_index < _vals.Length)
                {
                    ++_index;
                    return true;
                }

                return false;
            }

            public void Reset()
            {
                _index = 0;
            }

            public RunnableEntry Current => _vals[_index];

            object IEnumerator.Current => _vals[_index];

            public void Dispose() { }
        }
        #endregion

        public void Dispose()
        {
            foreach (var category in _runnables.Values)
            {
                foreach (var runnable in category)
                {
                    runnable.Class.Dispose();
                }
            }
        }
    }

    internal class RunnableEntry : IComparable<RunnableEntry>
    {
        public RunnableEntry(IRunnable @class, string name, string category)
        {
            Class = @class;
            Name = name;
            Category = category;
        }

        public IRunnable Class { get; private set; }
        public string Name { get; private set; }
        public string Category { get; private set; }

        public int CompareTo(RunnableEntry o)
        {
            return Name.CompareTo(o.Name);
        }
    }
}
