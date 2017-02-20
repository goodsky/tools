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
    public partial class GeneralReferences : PhoneApplicationPage
    {
        public GeneralReferences()
        {
            InitializeComponent();

            this.LocalScratchpad.DataContext = ScratchpadData.Instance;
        }

        private void ASCII_Tap(object sender, RoutedEventArgs e)
        {
            NavigationService.Navigate(new Uri("/FullPageImage.xaml?ImageUri=ASCIIReference.png", UriKind.Relative));
        }

        private void ASL_Tap(object sender, RoutedEventArgs e)
        {
            NavigationService.Navigate(new Uri("/FullPageImage.xaml?ImageUri=ASLReference.png", UriKind.Relative));
        }

        private void Braille_Tap(object sender, RoutedEventArgs e)
        {
            NavigationService.Navigate(new Uri("/FullPageImage.xaml?ImageUri=BrailleReference.png", UriKind.Relative));
        }

        private void Maritime_Tap(object sender, RoutedEventArgs e)
        {
            NavigationService.Navigate(new Uri("/FullPageImage.xaml?ImageUri=MaritimeFlagsReference.png", UriKind.Relative));
        }

        private void Morse_Tap(object sender, RoutedEventArgs e)
        {
            NavigationService.Navigate(new Uri("/FullPageImage.xaml?ImageUri=MorseReference.png", UriKind.Relative));
        }

        private void PigPen_Tap(object sender, RoutedEventArgs e)
        {
            NavigationService.Navigate(new Uri("/FullPageImage.xaml?ImageUri=PigPenReference.png", UriKind.Relative));
        }

        private void Rainbow_Tap(object sender, RoutedEventArgs e)
        {
            NavigationService.Navigate(new Uri("/FullPageImage.xaml?ImageUri=RainbowColorsReference.png", UriKind.Relative));
        }

        private void Resistor_Tap(object sender, RoutedEventArgs e)
        {
            NavigationService.Navigate(new Uri("/FullPageImage.xaml?ImageUri=ResistorColorCodeReference.png", UriKind.Relative));
        }

        private void Roman_Tap(object sender, RoutedEventArgs e)
        {
            NavigationService.Navigate(new Uri("/FullPageImage.xaml?ImageUri=RomanNumeralsReference.png", UriKind.Relative));
        }
    }
}