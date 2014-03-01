//
//  FarmProduceViewController.m
//  OurFarm
//
//  Created by tian hao on 13-7-8.
//  Copyright (c) 2013年 tian hao. All rights reserved.
//

#import "FarmProduceViewController.h"
#import "SummaryListViewController.h"

@interface FarmProduceViewController ()

@end

@implementation FarmProduceViewController

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

#pragma mark Segue
-(void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender{
    //传递参数
    NSNumber *type = [[NSNumber alloc] initWithInteger:0];
    UINavigationController *navigationController = [segue destinationViewController];
    
    //pickView
    if ([segue.identifier isEqualToString:@"pickView"]) {
        type = [[NSNumber alloc] initWithInteger:41];
        //获取navigationController 下的viewController
        SummaryListViewController *view = [navigationController viewControllers][0];
        if ([view respondsToSelector:@selector(setNearbyType:)]) {
            [view setValue:type forKey:@"nearbyType"];
        }
    }
    
    //marketView
    if ([segue.identifier isEqualToString:@"marketView"]) {
        type = [[NSNumber alloc] initWithInteger:42];
        //获取navigationController 下的viewController
        SummaryListViewController *view = [navigationController viewControllers][0];
        if ([view respondsToSelector:@selector(setNearbyType:)]) {
            [view setValue:type forKey:@"nearbyType"];
        }
    }
    
    //vegetableView
    if ([segue.identifier isEqualToString:@"vegetableView"]) {
        type = [[NSNumber alloc] initWithInteger:43];
        //获取navigationController 下的viewController
        SummaryListViewController *view = [navigationController viewControllers][0];
        if ([view respondsToSelector:@selector(setNearbyType:)]) {
            [view setValue:type forKey:@"nearbyType"];
        }
    }
    
    //eggView
    if ([segue.identifier isEqualToString:@"eggView"]) {
        type = [[NSNumber alloc] initWithInteger:44];
        //获取navigationController 下的viewController
        SummaryListViewController *view = [navigationController viewControllers][0];
        if ([view respondsToSelector:@selector(setNearbyType:)]) {
            [view setValue:type forKey:@"nearbyType"];
        }
    }
    
    //fishView
    if ([segue.identifier isEqualToString:@"fishView"]) {
        type = [[NSNumber alloc] initWithInteger:45];
        //获取navigationController 下的viewController
        SummaryListViewController *view = [navigationController viewControllers][0];
        if ([view respondsToSelector:@selector(setNearbyType:)]) {
            [view setValue:type forKey:@"nearbyType"];
        }
    }
    
    //duckView
    if ([segue.identifier isEqualToString:@"duckView"]) {
        type = [[NSNumber alloc] initWithInteger:46];
        //获取navigationController 下的viewController
        SummaryListViewController *view = [navigationController viewControllers][0];
        if ([view respondsToSelector:@selector(setNearbyType:)]) {
            [view setValue:type forKey:@"nearbyType"];
        }
    }
    
    
    
    }

@end
