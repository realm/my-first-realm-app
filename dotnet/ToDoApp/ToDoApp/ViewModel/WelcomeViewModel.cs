using System;
using System.ComponentModel;
using System.Diagnostics;
using System.Threading.Tasks;
using System.Windows.Input;
using Realms;
using Realms.Sync;
using Xamarin.Forms;

namespace ToDoApp
{
    // View model responsible for user sign up and sign in.
    public class WelcomeViewModel : INotifyPropertyChanged
    {
        private string _message;
        private string _username;
        private string _password;
        private bool _isFormEnabled = true;
        private INavigation _navigation;

        public string Username
        {
            get { return _username; }
            set
            {
                if (_username == value)
                {
                    return;
                }
                _username = value;
                OnPropertyChanged();
            }
        }

        public string Password
        {
            get { return _password; }
            set
            {
                if (_password == value)
                {
                    return;
                }
                _password = value;
                OnPropertyChanged();
            }
        }

        public string Message
        {
            get { return _message; }
            set
            {
                if (_message == value)
                {
                    return;
                }
                _message = value;
                OnPropertyChanged();
            }
        }

        public bool IsFormEnabled
        {
            get { return _isFormEnabled; }
            set
            {
                if (_isFormEnabled == value)
                {
                    return;
                }
                _isFormEnabled = value;
                OnPropertyChanged();
            }
        }

        public ICommand SignInCommand { get; private set; }
        public ICommand SignUpCommand { get; private set; }

        public event PropertyChangedEventHandler PropertyChanged;

        public WelcomeViewModel(INavigation navigation)
        {
            _navigation = navigation;
            SignInCommand = new Command(() => SignIn(false).IgnoreResult());
            SignUpCommand = new Command(() => SignIn(true).IgnoreResult());

            // Check if a user is already logged in on this device.
            var user = User.Current;
            if (user != null)
            {
                // User is already logged in. Skip to the items page.
                OpenRealmAndGoToItemsPage(user).IgnoreResult();
            }
        }

        private async Task SignIn(bool createUser)
        {
            // Prevent input and display the status message.
            IsFormEnabled = false;
            Message = "Signing in...";

            try
            {
                // Create credentials with the given username and password.
                // Specify whether we are registering a new user or not with `createUser`.
                var credentials = Credentials.UsernamePassword(Username, Password, createUser);

                // Log in as the user.
                var user = await User.LoginAsync(credentials, new Uri(Constants.AuthUrl));

                await OpenRealmAndGoToItemsPage(user);
            }
            catch (Exception ex)
            {
                // Display the error message.
                Message = ex.Message;
            }
            finally
            {
                // Be sure to re-enable the form in any case once finished here.
                IsFormEnabled = true;
            }
        }

        private async Task OpenRealmAndGoToItemsPage(User user)
        {
            // Yield in case this is called from the constructor,
            // at which point not everything is certain to be initialized.
            await Task.Yield();

            // Disable the form.
            IsFormEnabled = false;
            Message = "Signing in...";

            try
            {
                // Open the realm, then navigate to the items page.
                var realm = await OpenRealm(user);
                GoToItemsPage(realm);
            }
            catch (Exception ex)
            {
                // Display the error message.
                Message = ex.Message;
            }
            finally
            {
                // Be sure to re-enable the form in any case once finished here.
                IsFormEnabled = true;
            }
        }

        private async Task<Realm> OpenRealm(User user)
        {
            // Open the realm for the given user.
            Debug.Assert(user != null);

            var configuration = new FullSyncConfiguration(new Uri(Constants.RealmPath, UriKind.Relative), user);
            return await Realm.GetInstanceAsync(configuration);
        }

        private void GoToItemsPage(Realm realm)
        {
            // Given an opened realm, go to the items page.
            Debug.Assert(realm != null);

            Device.BeginInvokeOnMainThread(() =>
            {
                // Clear inputs and messages before leaving the page.
                IsFormEnabled = true;
                Username = "";
                Password = "";
                Message = "";
                _navigation.PushAsync(new ItemEntriesPage(realm));
            });
        }

        protected virtual void OnPropertyChanged(string propertyName = null)
        {
            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(propertyName));
        }
    }
}
