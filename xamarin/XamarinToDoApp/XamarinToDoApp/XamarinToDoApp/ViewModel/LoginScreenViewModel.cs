using System.ComponentModel;
using System.Windows.Input;
using Xamarin.Forms;
using Realms;
using Realms.Sync;
using System.Collections.Generic;
using System.Threading.Tasks;
using System;


namespace XamarinToDoApp
{

    public class LoginScreenViewModel : INotifyPropertyChanged
    {
        private string _username;

        public INavigation Navigation { get; set; }
        private Realm _realm;
        private bool _areCredentialsInvalid;
        public ICommand AuthenticateCommand { get; private set; }

        public event PropertyChangedEventHandler PropertyChanged;

        protected virtual void OnPropertyChanged(string propertyName = null)
        {
            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(propertyName));
        }


        public LoginScreenViewModel()
        {
            AuthenticateCommand = new Command(() =>
            {
                var credentials = Credentials.Nickname(Username, isAdmin: true);
                var user = Task.Run(() => User.LoginAsync(credentials, new Uri(Constants.AUTH_URL))).Result;
                var page = new ItemEntriesPage(new ItemEntriesViewModel());
                Navigation.PushAsync(page, true);
            });

        }

        public bool AreCredentialsInvalid
        {
            get => _areCredentialsInvalid;
            set
            {
                if (value == _areCredentialsInvalid) return;
                _areCredentialsInvalid = value;
                OnPropertyChanged(nameof(AreCredentialsInvalid));
            }
        }

        public string Username
        {
            get => _username;
            set
            {
                if (value == _username) return;
                _username = value;
                OnPropertyChanged(nameof(Username));
            }
        }


    }

}

