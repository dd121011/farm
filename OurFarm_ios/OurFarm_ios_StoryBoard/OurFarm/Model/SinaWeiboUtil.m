//
//  SinaWeiboUtil.m
//  OurFarm
//
//  Created by 李 凤勇 on 13-7-4.
//  Copyright (c) 2013年 tian hao. All rights reserved.
//

#import "SinaWeiboUtil.h"
#import "AppDelegate.h"
#import "SVProgressHUD.h"
#import "MyViewController.h"

@implementation SinaWeiboUtil

//获取SinaWeibo对象
- (SinaWeibo *)sinaweibo
{
    AppDelegate *delegate = (AppDelegate *)[UIApplication sharedApplication].delegate;
    return delegate.sinaweibo;
}

- (BOOL)isAuthValid {
    SinaWeibo *sinaweibo = [self sinaweibo];
    return sinaweibo.isAuthValid;
}
//注销本地认证数据
- (void)removeAuthData
{
    [[NSUserDefaults standardUserDefaults] removeObjectForKey:@"SinaWeiboAuthData"];
}
//保存本地认证数据
- (void)storeAuthData
{
    SinaWeibo *sinaweibo = [self sinaweibo];
    
    NSDictionary *authData = [NSDictionary dictionaryWithObjectsAndKeys:
                              sinaweibo.accessToken, @"AccessTokenKey",
                              sinaweibo.expirationDate, @"ExpirationDateKey",
                              sinaweibo.userID, @"UserIDKey",
                              sinaweibo.refreshToken, @"refresh_token", nil];
    [[NSUserDefaults standardUserDefaults] setObject:authData forKey:@"SinaWeiboAuthData"];
    [[NSUserDefaults standardUserDefaults] synchronize];
}

- (void)loginSinaWeibo
{    
    SinaWeibo *sinaweibo = [self sinaweibo];
    [sinaweibo logIn];
}

- (void)loginSinaWeibo :(UISwitch *)switchButton
{
    _switchButton = switchButton;
    [self loginSinaWeibo];
}

- (void)logoutSinaWeibo
{
    SinaWeibo *sinaweibo = [self sinaweibo];
    [sinaweibo logOut];
}

- (void)userInfoButtonPressed
{
    SinaWeibo *sinaweibo = [self sinaweibo];
    [sinaweibo requestWithURL:@"users/show.json"
                       params:[NSMutableDictionary dictionaryWithObject:sinaweibo.userID forKey:@"uid"]
                   httpMethod:@"GET"
                     delegate:self];
}

- (void)timelineButtonPressed
{
    SinaWeibo *sinaweibo = [self sinaweibo];
    [sinaweibo requestWithURL:@"statuses/user_timeline.json"
                       params:[NSMutableDictionary dictionaryWithObject:sinaweibo.userID forKey:@"uid"]
                   httpMethod:@"GET"
                     delegate:self];
}

- (void)postStatusButtonPressed
{
    
    UIAlertView *alertView = [[UIAlertView alloc] initWithTitle:@"Alert"
                                                        message:[NSString stringWithFormat:@"Will post status with text \"%@\"", @"123"]
                                                       delegate:self cancelButtonTitle:@"Cancel"
                                              otherButtonTitles:@"OK", nil];
    alertView.tag = 0;
    [alertView show];

}

- (void)alertView:(UIAlertView *)alertView willDismissWithButtonIndex:(NSInteger)buttonIndex
{
    if (buttonIndex == 1)
    {
        if (alertView.tag == 0)
        {
            // post status
            SinaWeibo *sinaweibo = [self sinaweibo];
            [sinaweibo requestWithURL:@"statuses/update.json"
                               params:[NSMutableDictionary dictionaryWithObjectsAndKeys:postStatusText, @"status", nil]
                           httpMethod:@"POST"
                             delegate:self];
            
        }
        else if (alertView.tag == 1)
        {
            // post image status
            SinaWeibo *sinaweibo = [self sinaweibo];
            
            [sinaweibo requestWithURL:@"statuses/upload.json"
                               params:[NSMutableDictionary dictionaryWithObjectsAndKeys:
                                       postImageStatusText, @"status",
                                       [UIImage imageNamed:@"logo.png"], @"pic", nil]
                           httpMethod:@"POST"
                             delegate:self];
            
        }
    }
}

#pragma mark - SinaWeibo Delegate

- (void)sinaweiboDidLogIn:(SinaWeibo *)sinaweibo
{
    NSLog(@"sinaweiboDidLogIn userID = %@ accesstoken = %@ expirationDate = %@ refresh_token = %@", sinaweibo.userID, sinaweibo.accessToken, sinaweibo.expirationDate,sinaweibo.refreshToken);
    
    [SVProgressHUD showSuccessWithStatus:@"新浪微博绑定成功"];
    [self storeAuthData];
    [_switchButton setOn:FALSE];
}

- (void)sinaweiboDidLogOut:(SinaWeibo *)sinaweibo
{
    NSLog(@"sinaweiboDidLogOut");
    
    [SVProgressHUD showSuccessWithStatus:@"新浪微博解除成功"];
    [self removeAuthData];
}

- (void)sinaweiboLogInDidCancel:(SinaWeibo *)sinaweibo
{
    NSLog(@"sinaweiboLogInDidCancel");
    [_switchButton setOn:FALSE animated:TRUE];
}

- (void)sinaweibo:(SinaWeibo *)sinaweibo logInDidFailWithError:(NSError *)error
{
    NSLog(@"sinaweibo logInDidFailWithError %@", error);
    [SVProgressHUD showErrorWithStatus:@"新浪微博绑定失败"];
    [_switchButton setOn:FALSE];
}

- (void)sinaweibo:(SinaWeibo *)sinaweibo accessTokenInvalidOrExpired:(NSError *)error
{
    NSLog(@"sinaweiboAccessTokenInvalidOrExpired %@", error);
    [self removeAuthData];
}

#pragma mark - SinaWeiboRequest Delegate

- (void)request:(SinaWeiboRequest *)request didFailWithError:(NSError *)error
{
    if ([request.url hasSuffix:@"users/show.json"])
    {
        userInfo = nil;
    }
    else if ([request.url hasSuffix:@"statuses/user_timeline.json"])
    {
        statuses = nil;
    }
    else if ([request.url hasSuffix:@"statuses/update.json"])
    {
        UIAlertView *alertView = [[UIAlertView alloc] initWithTitle:@"Alert"
                                                            message:[NSString stringWithFormat:@"Post status \"%@\" failed!", postStatusText]
                                                           delegate:nil cancelButtonTitle:nil otherButtonTitles:@"OK", nil];
        [alertView show];

        
        NSLog(@"Post status failed with error : %@", error);
    }
    else if ([request.url hasSuffix:@"statuses/upload.json"])
    {
        UIAlertView *alertView = [[UIAlertView alloc] initWithTitle:@"Alert"
                                                            message:[NSString stringWithFormat:@"Post image status \"%@\" failed!", postImageStatusText]
                                                           delegate:nil cancelButtonTitle:nil otherButtonTitles:@"OK", nil];
        [alertView show];
        
        NSLog(@"Post image status failed with error : %@", error);
    }
}

- (void)request:(SinaWeiboRequest *)request didFinishLoadingWithResult:(id)result
{
    if ([request.url hasSuffix:@"users/show.json"])
    {
        userInfo = result;
    }
    else if ([request.url hasSuffix:@"statuses/user_timeline.json"])
    {
        statuses = [result objectForKey:@"statuses"];
    }
    else if ([request.url hasSuffix:@"statuses/update.json"])
    {
        UIAlertView *alertView = [[UIAlertView alloc] initWithTitle:@"Alert"
                                                            message:[NSString stringWithFormat:@"Post status \"%@\" succeed!", [result objectForKey:@"text"]]
                                                           delegate:nil cancelButtonTitle:nil otherButtonTitles:@"OK", nil];
        [alertView show];

        
        postStatusText = nil;
    }
    else if ([request.url hasSuffix:@"statuses/upload.json"])
    {
        UIAlertView *alertView = [[UIAlertView alloc] initWithTitle:@"Alert"
                                                            message:[NSString stringWithFormat:@"Post image status \"%@\" succeed!", [result objectForKey:@"text"]]
                                                           delegate:nil cancelButtonTitle:nil otherButtonTitles:@"OK", nil];
        [alertView show];

         postImageStatusText = nil;
    }
}

@end
