//
//  AppDelegate.h
//  OurFarm dddd
//  OurFarm test  lify
//
//  Created by tian hao on 13-6-2.
//  Copyright (c) 2013年 tian hao. All rights reserved.
//

#import <UIKit/UIKit.h>
//sina weiwo start
#ifndef kAppKey
#define kAppKey @"2108655383"
#endif

#ifndef kAppSecret
#define kAppSecret @"fb294f83ad22bfba65d9b745a6bb8a0d"
#endif

#ifndef kAppRedirectURI
#define kAppRedirectURI @"http://www.sina.com"
#endif
//sina weiwo end

@class SinaWeibo;
@class MyViewController;

@interface AppDelegate : UIResponder <UIApplicationDelegate>
{
    SinaWeibo *sinaweibo;
    MyViewController *sinaWeiboShare;
}

@property (strong, nonatomic) UIWindow *window;
@property (readonly, nonatomic) SinaWeibo *sinaweibo;
@property (strong, nonatomic) MyViewController *sinaWeiboShare;

@end
