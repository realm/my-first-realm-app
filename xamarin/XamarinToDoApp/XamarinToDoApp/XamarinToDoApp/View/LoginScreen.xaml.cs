using System.ComponentModel;
using System.Windows.Input;
using Xamarin.Forms;
using Realms;
using Realms.Sync;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace XamarinToDoApp
{
    public partial class LoginScreen : ContentPage
    {
        public LoginScreen()
        {
            InitializeComponent();
            BindingContext = new LoginScreenViewModel{Navigation = Navigation};

        }
    }
}