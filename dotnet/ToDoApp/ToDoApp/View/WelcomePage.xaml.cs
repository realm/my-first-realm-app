using System;
using Realms;
using Xamarin.Forms;

namespace ToDoApp
{
    public partial class WelcomePage : ContentPage
    {
        public WelcomePage()
        {
            InitializeComponent();

            BindingContext = new WelcomeViewModel(Navigation);
        }
    }
}
