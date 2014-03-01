//
//  HomeViewController.m
//  OurFarm
//
//  Created by tian hao on 13-6-7.
//  Copyright (c) 2013年 tian hao. All rights reserved.
//

#import "HomeViewController.h"
#import "SummaryListViewController.h"
#import "SummaryListSearchViewController.h"
#import "SummaryListPlanViewController.h"
#import "DetailViewController.h"
#import "Summary.h"

@interface HomeViewController ()

@end

@implementation HomeViewController{
    @private NSMutableArray *_summaryArray ;
    //指定有五个元素初始化
    @private Summary *_recommendSummary ;
}


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
	[self loadRecommend:@"1" :@"1"];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark loadRecommend 

-(void) loadRecommend :(NSString *) type
                      :(NSString *) isused{
    NSLog(@"to loadRecommend  ");
    //请求参数
    NSDictionary *param = [NSDictionary dictionaryWithObjectsAndKeys:
                           [NSString stringWithString:type], @"type",
                           [NSString stringWithString:isused], @"isused",nil];
    
    [Summary getRecommendSummaryWithBlock:^(NSArray *posts, NSError *error) {
        if (error) {
            [[[UIAlertView alloc] initWithTitle:NSLocalizedString(@"Error", nil) message:[error localizedDescription] delegate:nil cancelButtonTitle:nil otherButtonTitles:NSLocalizedString(@"OK", nil), nil] show];
            NSLog(@"Get recommend summary error: %@", error);
        } else {
            // _posts = posts;
            if (posts != nil && [posts count] != 0) {
                //注意：homeview未初始view，@private变量未初始化，必须先初始化再赋值；
                NSMutableArray *mutablePosts = [[NSMutableArray alloc] init];
                for (Summary *summary in posts) {
                     NSLog(@"Summary -- %@ ", summary);
                    [mutablePosts addObject:summary];
                }
                _summaryArray = mutablePosts;
                NSLog(@"_summaryArray -- %@ ", _summaryArray);
                //取出第一个作为推荐值
                _recommendSummary = [_summaryArray objectAtIndex:0];
                NSLog(@"_recommendSummary -- %@ ", _recommendSummary);
                self.recommendName.text = _recommendSummary.name;

            }
        }
    }requestParameter:param];
    
    

}


#pragma mark Segue
-(void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender{
    //传递参数
    NSNumber *type = [[NSNumber alloc] initWithInteger:0];
    UINavigationController *navigationController = [segue destinationViewController];
    
    //导航到推荐
    if([segue.identifier isEqualToString:@"RecommendView"]){
        
        //获取navigationController 下的viewController
        NSLog(@"to RecommendView  ");
        DetailViewController *detailView = [navigationController viewControllers][0];
        if ([detailView respondsToSelector:@selector(setDestinationId:)]) {
            [detailView setValue:_recommendSummary.destinationId forKey:@"destinationId"];
            NSLog(@"detail id is :%@", _recommendSummary.destinationId );
        }
    }
    
    //导航到景点列表 
    if([segue.identifier isEqualToString:@"desView"]){
            type = [[NSNumber alloc] initWithInteger:1];
            //获取navigationController 下的viewController
            SummaryListViewController *view = [navigationController viewControllers][0];
            if ([view respondsToSelector:@selector(setNearbyType:)]) {
                [view setValue:type forKey:@"nearbyType"];
            }
        }
    
//    导航到农家乐列表
    if ([segue.identifier isEqualToString:@"farmView"]) {
            type = [[NSNumber alloc] initWithInteger:2];
            //获取navigationController 下的viewController
            SummaryListViewController *view = [navigationController viewControllers][0];
            if ([view respondsToSelector:@selector(setNearbyType:)]) {
            [view setValue:type forKey:@"nearbyType"];
            }
        }
    
//    导航到山庄列表
    if ([segue.identifier isEqualToString:@"villaView"]) {
        type = [[NSNumber alloc] initWithInteger:3];
        //获取navigationController 下的viewController
        SummaryListViewController *view = [navigationController viewControllers][0];
        if ([view respondsToSelector:@selector(setNearbyType:)]) {
            [view setValue:type forKey:@"nearbyType"];
        }
    }
    
    //    导航到休闲度假
    if ([segue.identifier isEqualToString:@"resortView"]) {
        type = [[NSNumber alloc] initWithInteger:20];
        //获取navigationController 下的viewController
        SummaryListPlanViewController *view = [navigationController viewControllers][0];
        if ([view respondsToSelector:@selector(setPlansType:)]) {
            [view setValue:type forKey:@"plansType"];
        }
    }
    
    //marketView
    if ([segue.identifier isEqualToString:@"marketView"]) {
        type = [[NSNumber alloc] initWithInteger:21];
        //获取navigationController 下的viewController
        SummaryListPlanViewController *view = [navigationController viewControllers][0];
        if ([view respondsToSelector:@selector(setPlansType:)]) {
            [view setValue:type forKey:@"plansType"];
        }
    }
    
    //childView
    if ([segue.identifier isEqualToString:@"childView"]) {
        type = [[NSNumber alloc] initWithInteger:7];
        //获取navigationController 下的viewController
        SummaryListPlanViewController *view = [navigationController viewControllers][0];
        if ([view respondsToSelector:@selector(setPlansType:)]) {
            [view setValue:type forKey:@"plansType"];
        }
    }
    
    //pickView
    if ([segue.identifier isEqualToString:@"pickView"]) {
        type = [[NSNumber alloc] initWithInteger:4];
        //获取navigationController 下的viewController
        SummaryListPlanViewController *view = [navigationController viewControllers][0];
        if ([view respondsToSelector:@selector(setPlansType:)]) {
            [view setValue:type forKey:@"plansType"];
        }
    }
    
    //mountainView
    if ([segue.identifier isEqualToString:@"mountainView"]) {
        type = [[NSNumber alloc] initWithInteger:2];
        //获取navigationController 下的viewController
        SummaryListPlanViewController *view = [navigationController viewControllers][0];
        if ([view respondsToSelector:@selector(setPlansType:)]) {
            [view setValue:type forKey:@"plansType"];
        }
    }
    
    //fishingView
    if ([segue.identifier isEqualToString:@"fishingView"]) {
        type = [[NSNumber alloc] initWithInteger:5];
        //获取navigationController 下的viewController
        SummaryListPlanViewController *view = [navigationController viewControllers][0];
        if ([view respondsToSelector:@selector(setPlansType:)]) {
            [view setValue:type forKey:@"plansType"];
        }
    }
    
}




@end
