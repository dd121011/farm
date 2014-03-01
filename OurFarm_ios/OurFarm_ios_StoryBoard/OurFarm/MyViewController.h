//
//  MyViewController.h
//  OurFarm
//
//  我的页面
//
//  Created by 李 凤勇 on 13-6-26.
//  Copyright (c) 2013年 tian hao. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ShareViewController.h"
#import "SinaWeibo.h"
#import "SinaWeiboRequest.h"
#import "TCWBEngine.h"

//@class SinaWeiboUtil;

@interface MyViewController : UIViewController<SinaWeiboDelegate, SinaWeiboRequestDelegate>
{
    TCWBEngine *weiboEngine;
    NSDictionary *userInfo;
    NSArray *statuses;
    NSString *postStatusText;
    NSString *postImageStatusText;
}

//@property (readonly, nonatomic) SinaWeiboUtil *sinaWeiboUtil;

@property (strong, nonatomic) IBOutlet UISwitch *sinaWeiboSwitch;
@property (strong, nonatomic) IBOutlet UISwitch *tencentWeiboSwitch;

@property (nonatomic, retain) TCWBEngine *weiboEngine;

- (IBAction)sianWeiboAction:(id)sender;
- (IBAction)tencentWeiboAction:(id)sender;

- (SinaWeibo *)sinaweibo;

@end
