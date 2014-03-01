//
//  OrderViewController.m
//  OurFarm
//
//  Created by tian hao on 13-6-8.
//  Copyright (c) 2013年 tian hao. All rights reserved.
//

#import "OrderViewController.h"

@interface OrderViewController ()

@end

@implementation OrderViewController
@synthesize page1Data; 

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

-(void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender{
    NSString* data = page1Data.text;
    UIViewController* view = segue.destinationViewController;
    if ([view respondsToSelector:@selector(setParam:)]) {
        [view setValue:data forKey:@"param"];
    }
}

@end
