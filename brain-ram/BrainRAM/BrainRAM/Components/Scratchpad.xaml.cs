using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Navigation;
using Microsoft.Phone.Controls;
using Microsoft.Phone.Shell;
using System.ComponentModel;

namespace BrainRAM
{
    public partial class Scratchpad : UserControl
    {
        // Scratchpad component
        public Scratchpad()
        {
            InitializeComponent();
        }

        // Overwrite default text on a single tap
        private void TextBox_Tap(object sender, System.Windows.Input.GestureEventArgs e)
        {
            if (ScratchpadData.Instance.Data.Equals(ScratchpadData.DefaultStartingText))
            {
                ScratchpadData.Instance.Data = "";
            }
        }
    }

    public class ScratchpadData : INotifyPropertyChanged
    {
        public static string DefaultStartingText = "Use this area for scratchpad work...";

        // Static scratchpad data for DataContext binding
        public static ScratchpadData Instance = new ScratchpadData();

        // The scratchpad data
        public string data;
        public string Data
        {
            get
            {
                return this.data;
            }

            set
            {
                this.data = value;
                LocalPropertyChanged("Data");
            }
        }

        public ScratchpadData()
        {
            this.data = ScratchpadData.DefaultStartingText;
        }

        public event PropertyChangedEventHandler PropertyChanged;

        private void LocalPropertyChanged(string propertyName)
        {
            if (PropertyChanged != null)
            {
                PropertyChanged(this, new PropertyChangedEventArgs(propertyName));
            }
        }
    }
}
