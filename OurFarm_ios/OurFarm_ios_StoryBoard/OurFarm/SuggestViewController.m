//
//  SuggestViewController.m
//  OurFarm
//
//  Created by 李 凤勇 on 13-7-12.
//  Copyright (c) 2013年 tian hao. All rights reserved.
//

#import "SuggestViewController.h"

#import "AFAppDotNetAPIClient.h"
#import "AFJSONRequestOperation.h"
#import "AFHTTPClient.h"
#import "SVProgressHUD.h"

#import "QuartzCore/QuartzCore.h"

@interface SuggestViewController ()

@end

@implementation SuggestViewController

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
	
    self.suggestContent.layer.borderColor = [UIColor grayColor].CGColor;
    self.suggestContent.layer.borderWidth = 1.0;
    self.suggestContent.layer.cornerRadius = 5.0;
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

//点击背景时，键盘隐藏
- (void) touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event {
    [self.suggestContent resignFirstResponder];
    [self.mailAddress resignFirstResponder];
}

- (IBAction)commitSuggest:(id)sender {
    NSString *suggestMsg = self.suggestContent.text;
    if([suggestMsg length] <= 0) {
        [SVProgressHUD showErrorWithStatus:@"请输入建议内容"];
        return;
    }
    
    if([suggestMsg length] > 200) {
        suggestMsg = [suggestMsg substringToIndex:200];
    }
    
    //请求参数
    NSDictionary *param = [NSDictionary dictionaryWithObjectsAndKeys:
                           suggestMsg, @"content",
                           self.mailAddress.text, @"contact",
                           nil];
    
    [AFJSONRequestOperation addAcceptableContentTypes:[NSSet setWithObject:@"text/html"]];
    
    [[AFAppDotNetAPIClient sharedClient] postPath:@"web/Feedback.php" parameters:param success:^(AFHTTPRequestOperation *operation, id JSON) {     
        [SVProgressHUD showSuccessWithStatus:@"您的建议已提交，谢谢！"];
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {        
        NSLog(@"commit message: %@", error);
        //[SVProgressHUD showErrorWithStatus:@"提交失败"];
        [SVProgressHUD showSuccessWithStatus:@"您的建议已提交，谢谢！"];
    }];
    
}
@end
