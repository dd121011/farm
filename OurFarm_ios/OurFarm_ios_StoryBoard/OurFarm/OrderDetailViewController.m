//
//  OrderDetailViewController.m
//  OurFarm
//
//  Created by tian hao on 13-6-8.
//  Copyright (c) 2013年 tian hao. All rights reserved.
//

#import "OrderDetailViewController.h"

@interface OrderDetailViewController ()

@end

@implementation OrderDetailViewController
@synthesize param;
@synthesize page2Data;

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
    page2Data.text=param; 
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}
- (IBAction)closeWin:(id)sender {
    [self dismissViewControllerAnimated:YES completion:nil];  
}

@end
