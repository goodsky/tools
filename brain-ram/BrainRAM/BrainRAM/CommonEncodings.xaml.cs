using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Navigation;
using Microsoft.Phone.Controls;
using Microsoft.Phone.Shell;

namespace BrainRAM
{
    public partial class CommonEncodings : PhoneApplicationPage
    {
        public CommonEncodings()
        {
            InitializeComponent();
            this.InitializeBrailleDictionary();

            this.LocalScratchpad1.DataContext = ScratchpadData.Instance;
            this.LocalScratchpad2.DataContext = ScratchpadData.Instance;
            this.LocalScratchpad3.DataContext = ScratchpadData.Instance;
            this.LocalScratchpad4.DataContext = ScratchpadData.Instance;
            this.LocalScratchpad5.DataContext = ScratchpadData.Instance;
        }

        // ***************************************************************
        // Braille Section
        // ***************************************************************
        // Braille Character class. Just a bool array with length 6.
        public class BrailleCharacter
        {
            bool[] dots;

            public BrailleCharacter(String brailleString)
            {
                if (brailleString.Length != 6)
                    throw new Exception("Invalid Braille Character!! " + brailleString);

                dots = new bool[6];
                for (int i = 0; i < 6; ++i)
                {
                    if (brailleString[i] == '0')
                        dots[i] = false;
                    else if (brailleString[i] == '1')
                        dots[i] = true;
                    else
                        throw new Exception("Invalid Braille Character!!! " + brailleString);
                }
            }
        }

        // Create Braille Dictionary
        DoubleDictionary<char, BrailleCharacter> Braille = new DoubleDictionary<char, BrailleCharacter>();
        public void InitializeBrailleDictionary()
        {
            Braille.AddEncoding('A', new BrailleCharacter("100000"));
            Braille.AddEncoding('B', new BrailleCharacter("110000"));
            Braille.AddEncoding('C', new BrailleCharacter("100100"));
            Braille.AddEncoding('D', new BrailleCharacter("100110"));
            Braille.AddEncoding('E', new BrailleCharacter("100010"));
            Braille.AddEncoding('F', new BrailleCharacter("110100"));
            Braille.AddEncoding('G', new BrailleCharacter("110110"));
            Braille.AddEncoding('H', new BrailleCharacter("110010"));
            Braille.AddEncoding('I', new BrailleCharacter("010100"));
            Braille.AddEncoding('J', new BrailleCharacter("010110"));
            Braille.AddEncoding('K', new BrailleCharacter("101000"));
            Braille.AddEncoding('L', new BrailleCharacter("111000"));
            Braille.AddEncoding('M', new BrailleCharacter("101100"));
            Braille.AddEncoding('N', new BrailleCharacter("101110"));
            Braille.AddEncoding('O', new BrailleCharacter("101010"));
            Braille.AddEncoding('P', new BrailleCharacter("111100"));
            Braille.AddEncoding('Q', new BrailleCharacter("111110"));
            Braille.AddEncoding('R', new BrailleCharacter("111010"));
            Braille.AddEncoding('S', new BrailleCharacter("011100"));
            Braille.AddEncoding('T', new BrailleCharacter("011110"));
            Braille.AddEncoding('U', new BrailleCharacter("101001"));
            Braille.AddEncoding('V', new BrailleCharacter("111001"));
            Braille.AddEncoding('W', new BrailleCharacter("010111"));
            Braille.AddEncoding('X', new BrailleCharacter("101101"));
            Braille.AddEncoding('Y', new BrailleCharacter("101111"));
            Braille.AddEncoding('Z', new BrailleCharacter("101011"));
            Braille.AddEncoding('#', new BrailleCharacter("001111"));
            Braille.AddEncoding('.', new BrailleCharacter("010011"));
            Braille.AddEncoding(',', new BrailleCharacter("010000"));
            Braille.AddEncoding('!', new BrailleCharacter("011010"));
            Braille.AddEncoding('?', new BrailleCharacter("010011"));
            Braille.AddEncoding('-', new BrailleCharacter("001001"));
            Braille.AddEncoding('"', new BrailleCharacter("011001"));
            Braille.AddEncoding(':', new BrailleCharacter("010010"));
            Braille.AddEncoding(';', new BrailleCharacter("011000"));
        }

        /// <summary>
        /// Click one of the braille dots
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void BrailleDot_Click(object sender, RoutedEventArgs e)
        {

        }

        /// <summary>
        /// Tap the Answer character to append it to the end of the scratchpad
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void BrailleAnswer_Tap(object sender, System.Windows.Input.GestureEventArgs e)
        {

        }

        /// <summary>
        /// Convert scratchpad to braille
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void ScratchpadToBraille_Click(object sender, RoutedEventArgs e)
        {

        }

        // Reference Button Clicks
        private void BrailleReference_Click(object sender, RoutedEventArgs e)
        {
            NavigationService.Navigate(new Uri("/FullPageImage.xaml?ImageUri=BrailleReference.png", UriKind.Relative));
        }
        private void MorseReference_Click(object sender, RoutedEventArgs e)
        {
            NavigationService.Navigate(new Uri("/FullPageImage.xaml?ImageUri=MorseReference.png", UriKind.Relative));
        }
        private void SemaphoreReference_Click(object sender, RoutedEventArgs e)
        {
            NavigationService.Navigate(new Uri("/FullPageImage.xaml?ImageUri=SemaphoreReference.png", UriKind.Relative));
        }
        private void RomanReference_Click(object sender, RoutedEventArgs e)
        {
            NavigationService.Navigate(new Uri("/FullPageImage.xaml?ImageUri=RomanNumeralsReference.png", UriKind.Relative));
        }
        private void ASCIIReference_Click(object sender, RoutedEventArgs e)
        {
            NavigationService.Navigate(new Uri("/FullPageImage.xaml?ImageUri=ASCIIReference.png", UriKind.Relative));
        }
    }
}