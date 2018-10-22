using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Plugin.LocalNotifications;
using Xamarin.Forms;

namespace Lab1
{
    public partial class MainPage : ContentPage
    {
        public MainPage()
        {
            InitializeComponent();
        }

        void PushNotifBtnClicked(object sender, EventArgs e)
        {
            CrossLocalNotifications.Current.Show(
                title: "Very notification",
                body: "Much notification"
            );
        }
    }
}
