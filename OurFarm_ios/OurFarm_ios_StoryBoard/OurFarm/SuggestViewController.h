//
//  SuggestViewController.h
//  OurFarm
//
//  Created by 李 凤勇 on 13-7-12.
//  Copyright (c) 2013年 tian hao. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface SuggestViewController : UIViewController

@property (strong, nonatomic) IBOutlet UITextView *suggestContent;//建议内容
@property (strong, nonatomic) IBOutlet UITextField *mailAddress;//邮箱地址

- (IBAction)commitSuggest:(id)sender;//提交

@end
