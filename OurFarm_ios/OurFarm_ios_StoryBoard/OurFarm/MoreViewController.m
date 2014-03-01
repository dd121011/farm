//
//  MoreViewController.m
//  OurFarm
//
//  Created by 李 凤勇 on 13-7-11.
//  Copyright (c) 2013年 tian hao. All rights reserved.
//

#import "MoreViewController.h"
#import "FarmHomeDB.h"

#import "WZGuideViewController.h"
#import "MTPopupWindow.h"
#import "SVProgressHUD.h"

@interface MoreViewController ()

@end

@implementation MoreViewController

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
	// Do any additional setup after loading the view.
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}
//更新到本地缓存
- (IBAction)updateLocalData:(id)sender {
    //TODO 该功能是否还要？
    [SVProgressHUD showSuccessWithStatus:@"已经更新成功！"];
}
//清空缓存
- (IBAction)cleanCatche:(id)sender {
    [FarmHomeDB cleanCatche];
    [SVProgressHUD showSuccessWithStatus:@"缓存清空成功！"];
}
//检查最新版本
- (IBAction)checkNewVersion:(id)sender {
    //TODO 添加增加更新功能
    [SVProgressHUD showSuccessWithStatus:@"您当前已经时是最新版本了！"];
}
//免责声明
- (IBAction)disclaimer:(id)sender {
    [MTPopupWindow showWindowWithHTMLFile:@"disclaimer.html"];
}
//给我们建议
- (IBAction)suggestToUs:(id)sender {
    
}
//联系我们
- (IBAction)contactToUs:(id)sender {
    [MTPopupWindow showWindowWithHTMLFile:@"contactToUs.html"];
}
//关于我们
- (IBAction)aboutUs:(id)sender {
    [WZGuideViewController show];
}
@end
