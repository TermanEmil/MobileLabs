using System;
using Foundation;
using ObjCRuntime;
using UserNotifications;

namespace Lab1.iOS
{
    public class UserNotificationCenterDelegate : UNUserNotificationCenterDelegate
    {
        [Export("userNotificationCenter:willPresentNotification:withCompletionHandler:")]
        public override void WillPresentNotification(
            UNUserNotificationCenter center,
            UNNotification notification,
            Action<UNNotificationPresentationOptions> completionHandler)
        {
            completionHandler(UNNotificationPresentationOptions.Alert);
        }
    }
}
