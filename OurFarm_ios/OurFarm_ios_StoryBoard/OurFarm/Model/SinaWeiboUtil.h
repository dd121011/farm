//
//  SinaWeiboUtil.h
//  OurFarm
//
//  Created by 李 凤勇 on 13-7-4.
//  Copyright (c) 2013年 tian hao. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "SinaWeibo.h"
#import "SinaWeiboRequest.h"

#import <UIKit/UIKit.h>

@interface SinaWeiboUtil : NSObject<SinaWeiboDelegate, SinaWeiboRequestDelegate>
{
    NSDictionary *userInfo;
    NSArray *statuses;
    NSString *postStatusText;
    NSString *postImageStatusText;
}

@property(retain, nonatomic)UISwitch *switchButton;

- (SinaWeibo *)sinaweibo;
- (BOOL)isAuthValid;
- (void)removeAuthData;
- (void)storeAuthData;

- (void)loginSinaWeibo;
- (void)loginSinaWeibo :(UISwitch *)switchButton;
- (void)logoutSinaWeibo;

@end
