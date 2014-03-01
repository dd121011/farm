//
//  MoreViewController.h
//  OurFarm
//
//  Created by 李 凤勇 on 13-7-11.
//  Copyright (c) 2013年 tian hao. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface MoreViewController : UIViewController

- (IBAction)updateLocalData:(id)sender;//更新到本地缓存
- (IBAction)cleanCatche:(id)sender;//清空缓存

- (IBAction)checkNewVersion:(id)sender;//检查最新版本
- (IBAction)disclaimer:(id)sender;//免责声明
- (IBAction)suggestToUs:(id)sender;//给我们建议
- (IBAction)contactToUs:(id)sender;//联系我们
- (IBAction)aboutUs:(id)sender;//关于我们

@end
