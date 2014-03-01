//
//  ShareViewController.m
//  OurFarm
//
//  Created by 李 凤勇 on 13-7-5.
//  Copyright (c) 2013年 tian hao. All rights reserved.
//

#import "ShareViewController.h"
#import "AppDelegate.h"
#import "MyViewController.h"
#import "SVProgressHUD.h"

#import "QuartzCore/QuartzCore.h"

@interface ShareViewController ()

@end

@implementation ShareViewController

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
    
    self.shareContent.text = self.shareText;
    
    self.shareContent.layer.borderColor = [UIColor grayColor].CGColor;
    self.shareContent.layer.borderWidth = 1.0;
    self.shareContent.layer.cornerRadius = 5.0;
    
    //self.myViewController = [[MyViewController alloc] init];
    
    TCWBEngine *engine = [[TCWBEngine alloc] initWithAppKey:WiressSDKDemoAppKey andSecret:WiressSDKDemoAppSecret andRedirectUrl:REDIRECTURI];
    [engine setRootViewController:self];
    self.weiboEngine = engine;
    
    if([self isAuthValid]) {
        NSLog(@"sinaweibo ON");
        self.sinaWeiboCanShare = YES;
    } else {
        NSLog(@"sinaweibo OFF");
        self.sinaWeiboCanShare = NO;
        [self.sinaWeiboBtn setImage:[UIImage imageNamed:@"share_weibo32.png"]
                              forState:UIControlStateNormal];
    }
    
    if ([[self.weiboEngine accessToken] length] > 0) {
        NSLog(@"QQweibo ON");
        self.tencentWeiboCanShare = YES;
    } else {
        NSLog(@"QQweibo OFF");
        UIImage *image = [UIImage imageNamed:@"share_weibo32.png"];
        self.tencentWeiboCanShare = NO;
        [self.tencentWeiboBtn setImage:[UIImage imageNamed:@"share_weibo32.png"]
                              forState:UIControlStateNormal];
    }
    
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

//点击背景时，键盘隐藏
- (void) touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event {
    [self.shareContent resignFirstResponder];
}

//获取SinaWeibo对象
- (IBAction)sinaWeiboClick:(id)sender {
    if(self.sinaWeiboCanShare == YES) {
        self.sinaWeiboCanShare = NO;
        [self.sinaWeiboBtn setImage:[UIImage imageNamed:@"share_weibo32.png"]
                              forState:UIControlStateNormal];
    } else {
        if([self isAuthValid]){
            self.sinaWeiboCanShare = YES;
            [self.sinaWeiboBtn setImage:[UIImage imageNamed:@"sina_weiboicon32.png"]
                               forState:UIControlStateNormal];
        } else {
            SinaWeibo *sinaweibo = [self sinaweibo];
            [sinaweibo logIn];
            //TODO 回调的是myviewcontroller的方法，不知道如何捕获调用失败的情况。。。。
            self.sinaWeiboCanShare = YES;
            [self.sinaWeiboBtn setImage:[UIImage imageNamed:@"sina_weiboicon32.png"]
                               forState:UIControlStateNormal];
        }
    }
}

- (IBAction)tencentWeiboClick:(id)sender {
    if(self.tencentWeiboCanShare == YES) {
        self.tencentWeiboCanShare = NO;
        [self.tencentWeiboBtn setImage:[UIImage imageNamed:@"share_weibo32.png"]
                           forState:UIControlStateNormal];
    } else {
        if([[self.weiboEngine accessToken] length] > 0){
            self.tencentWeiboCanShare = YES;
            [self.tencentWeiboBtn setImage:[UIImage imageNamed:@"tencent_weiboicon32.png"]
                               forState:UIControlStateNormal];
        } else {
            //TODO 为什么不弹出登陆窗口
            [weiboEngine logInWithDelegate:self
                                 onSuccess:@selector(onSuccessLogin)
                                 onFailure:@selector(onFailureLogin:)];
        }
    }
}

- (IBAction)shareBtnClick:(id)sender {
    if(!self.sinaWeiboCanShare && !self.tencentWeiboCanShare) {
        [SVProgressHUD showErrorWithStatus:@"请选择分享的目标"];
        return;
    }
    
    //share sina weibo
    if(self.sinaWeiboCanShare && [self isAuthValid]) {
        // post status
        SinaWeibo *sinaweibo = [self sinaweibo];
        [sinaweibo requestWithURL:@"statuses/update.json"
                           params:[NSMutableDictionary dictionaryWithObjectsAndKeys:self.shareText, @"status", nil]
                       httpMethod:@"POST"
                         delegate:self];
    }
    
    //share tencent weibo
    if(self.tencentWeiboCanShare && [[self.weiboEngine accessToken] length] > 0) {
        //发表一条微博
        [self.weiboEngine  postTextTweetWithFormat:@"json"
                                  content:self.shareText
                                 clientIP:@"10.10.1.31"
                                longitude:nil
                              andLatitude:nil
                              parReserved:nil
                                 delegate:self
                                onSuccess:@selector(successCallBack:)
                                onFailure:@selector(failureCallBack:)];
    }
}

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


#pragma mark - SinaWeibo Delegate

- (void)sinaweiboDidLogIn:(SinaWeibo *)sinaweibo
{
    NSLog(@"sinaweiboDidLogIn userID = %@ accesstoken = %@ expirationDate = %@ refresh_token = %@", sinaweibo.userID, sinaweibo.accessToken, sinaweibo.expirationDate,sinaweibo.refreshToken);
    
    //[SVProgressHUD showSuccessWithStatus:@"新浪微博绑定成功"];
    [self storeAuthData];
    
    self.sinaWeiboCanShare = YES;
    [self.sinaWeiboBtn setImage:[UIImage imageNamed:@"sina_weiboicon32.png"]
                       forState:UIControlStateNormal];
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
}

- (void)sinaweibo:(SinaWeibo *)sinaweibo logInDidFailWithError:(NSError *)error
{
    NSLog(@"sinaweibo logInDidFailWithError %@", error);
    [SVProgressHUD showErrorWithStatus:@"新浪微博绑定失败"];
}

- (void)sinaweibo:(SinaWeibo *)sinaweibo accessTokenInvalidOrExpired:(NSError *)error
{
    NSLog(@"sinaweiboAccessTokenInvalidOrExpired %@", error);
    [self removeAuthData];
}

#pragma mark - SinaWeiboRequest Delegate

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
        [SVProgressHUD showErrorWithStatus:@"新浪微博发送成功"];
//        UIAlertView *alertView = [[UIAlertView alloc] initWithTitle:@"Alert"
//                                                            message:[NSString stringWithFormat:@"Post status \"%@\" succeed!", [result objectForKey:@"text"]]
//                                                           delegate:nil cancelButtonTitle:nil otherButtonTitles:@"OK", nil];
//        [alertView show];
//        
//        
//        postStatusText = nil;
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


#pragma mark - creatSuccessOrFail
- (void)successCallBack:(id)result{
    [SVProgressHUD showSuccessWithStatus:@"腾讯微博发送成功"];
}

- (void)failureCallBack{
    [SVProgressHUD showErrorWithStatus:@"腾讯微博发送失败"];
}


//腾讯微博登录成功回调
- (void)onSuccessLogin
{
    self.tencentWeiboCanShare = YES;
    [self.tencentWeiboBtn setImage:[UIImage imageNamed:@"tencent_weiboicon32.png"]
                          forState:UIControlStateNormal];
}

//腾讯微博登录失败回调
- (void)onFailureLogin:(NSError *)error
{
    [SVProgressHUD showErrorWithStatus:@"腾讯微博绑定失败"];
}

@end
