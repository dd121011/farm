//
//  MyViewController.m
//  OurFarm
//
//  我的页面
//
//  Created by 李 凤勇 on 13-6-26.
//  Copyright (c) 2013年 tian hao. All rights reserved.
//

#import "MyViewController.h"
#import "MyListViewController.h"

#import "AppDelegate.h"

#import "SVProgressHUD.h"

@interface MyViewController ()

@end

@implementation MyViewController

@synthesize weiboEngine;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    if([self isAuthValid]) {
        [_sinaWeiboSwitch setOn:TRUE];
    } else {
        [_sinaWeiboSwitch setOn:FALSE];
    }
    
    if ([[self.weiboEngine accessToken] length] > 0) {
        [_tencentWeiboSwitch setOn:TRUE];
    } else {
        [_tencentWeiboSwitch setOn:FALSE];
    }
    
    TCWBEngine *engine = [[TCWBEngine alloc] initWithAppKey:WiressSDKDemoAppKey andSecret:WiressSDKDemoAppSecret andRedirectUrl:REDIRECTURI];
    [engine setRootViewController:self];
    self.weiboEngine = engine;
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender{
    //传递参数
    NSNumber *type = [[NSNumber alloc] initWithInteger:0];
    UINavigationController *navigationController = [segue destinationViewController];
    
    //导航到我的收藏列表
    if([segue.identifier isEqualToString:@"favorite"]){
        type = [[NSNumber alloc] initWithInteger:1];
    }
    //导航到我的行程列表
    if([segue.identifier isEqualToString:@"trip"]){
        type = [[NSNumber alloc] initWithInteger:2];
    }
    
    MyListViewController *view = [navigationController viewControllers][0];
    if ([view respondsToSelector:@selector(setMyType:)]) {
        [view setValue:type forKey:@"myType"];
    }
    
}

//新浪微博绑定判断
- (IBAction)sianWeiboAction:(id)sender {
    UISwitch *sinaSwitch = (UISwitch *)sender;
    //sinaSwitch
    BOOL isButtonOn = [sinaSwitch isOn];
    if(isButtonOn) {
        SinaWeibo *sinaweibo = [self sinaweibo];
        [sinaweibo logIn];
        NSLog(@"sianWeiboAction: ON....ON");
    } else {
        SinaWeibo *sinaweibo = [self sinaweibo];
        [self logoutSinaWeibo];
         NSLog(@"sianWeiboAction: OFF....OFF");
    }
    
}
//腾讯微博绑定判断
- (IBAction)tencentWeiboAction:(id)sender {
    UISwitch *tencentSwitch = (UISwitch *)sender;
    BOOL isButtonOn = [tencentSwitch isOn];
    if(isButtonOn) {
        NSLog(@"tencentWeiboAction: ON....ON");
        [weiboEngine logInWithDelegate:self
                             onSuccess:@selector(onSuccessLogin)
                             onFailure:@selector(onFailureLogin:)];
    } else {
        NSLog(@"tencentWeiboAction: OFF....OFF");
        if ([weiboEngine logOut]) {
            [SVProgressHUD showSuccessWithStatus:@"腾讯微博登出成功"];
        }else {
            [SVProgressHUD showErrorWithStatus:@"腾讯微博登出失败"];
        }
    }
}

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
}

- (void)sinaweiboDidLogOut:(SinaWeibo *)sinaweibo
{
    NSLog(@"sinaweiboDidLogOut");
    
    [SVProgressHUD showSuccessWithStatus:@"新浪微博解除成功"];
    [self removeAuthData];
}

- (void)sinaweiboLogInDidCancel:(SinaWeibo *)sinaweibo
{
    //TODO 取消后 状态回不去？
    NSLog(@"sinaweiboLogInDidCancel 123");
    [_sinaWeiboSwitch setOn:FALSE animated:TRUE];
}

- (void)sinaweibo:(SinaWeibo *)sinaweibo logInDidFailWithError:(NSError *)error
{
    NSLog(@"sinaweibo logInDidFailWithError %@", error);
    [SVProgressHUD showErrorWithStatus:@"新浪微博绑定失败"];
    [_sinaWeiboSwitch setOn:FALSE animated:TRUE];
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

//腾讯微博登录成功回调
- (void)onSuccessLogin
{
    [SVProgressHUD showSuccessWithStatus:@"腾讯微博绑定成功"];
}

//腾讯微博登录失败回调
- (void)onFailureLogin:(NSError *)error
{
    [SVProgressHUD showErrorWithStatus:@"腾讯微博绑定失败"];
    [_tencentWeiboSwitch setOn:FALSE animated:TRUE];
}

@end
