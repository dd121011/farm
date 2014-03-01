//
//  ShareViewController.h
//  OurFarm
//
//  Created by 李 凤勇 on 13-7-5.
//  Copyright (c) 2013年 tian hao. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "SinaWeibo.h"
#import "SinaWeiboRequest.h"
#import "TCWBEngine.h"

@class MyViewController;

@interface ShareViewController : UIViewController<SinaWeiboDelegate, SinaWeiboRequestDelegate>
{
    TCWBEngine *weiboEngine;
    NSDictionary *userInfo;
    NSArray *statuses;
    NSString *postStatusText;
    NSString *postImageStatusText;
}

@property (nonatomic, retain) TCWBEngine *weiboEngine;

@property (nonatomic) NSString *shareText;
@property (strong, nonatomic) IBOutlet UITextView *shareContent;
@property (strong, nonatomic) IBOutlet UIButton *sinaWeiboBtn;
@property (strong, nonatomic) IBOutlet UIButton *tencentWeiboBtn;
@property (strong, nonatomic) IBOutlet UIButton *shareBtn;

@property (retain, nonatomic) MyViewController *myViewController;
@property (nonatomic) BOOL sinaWeiboCanShare;
@property (nonatomic) BOOL tencentWeiboCanShare;


- (IBAction)sinaWeiboClick:(id)sender;
- (IBAction)tencentWeiboClick:(id)sender;
- (IBAction)shareBtnClick:(id)sender;

- (SinaWeibo *)sinaweibo;
- (BOOL)isAuthValid;

- (void)removeAuthData;
- (void)storeAuthData;

- (void)loginSinaWeibo;
- (void)logoutSinaWeibo;

- (void)sinaweiboLogInDidCancel:(SinaWeibo *)sinaweibo;

@end
