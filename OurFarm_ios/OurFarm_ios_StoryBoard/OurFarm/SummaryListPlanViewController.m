//
//  SummaryListPlanViewViewController.m
//  OurFarm
//
//  Created by tian hao on 13-6-16.
//  Copyright (c) 2013年 tian hao. All rights reserved.
//

#import "SummaryListPlanViewController.h"
#import "SummaryCell.h"
#import "summary.h"
#import "DetailViewController.h"
#import "MapViewController.h"

@interface SummaryListPlanViewController ()

@end

@implementation SummaryListPlanViewController{
    @private NSMutableArray *_summaryArray;
}
@synthesize plansType;

- (id)initWithStyle:(UITableViewStyle)style
{
    self = [super initWithStyle:style];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    reloads_ = -1;
    
    _summaryArray = [[NSMutableArray alloc] init];
    pullToRefreshManager_ = [[MNMBottomPullToRefreshManager alloc] initWithPullToRefreshViewHeight:60.0f tableView:_table withClient:self];
    
    //获得参数
    NSLog(@" plantype is :%d",[self.plansType integerValue]);
    [self loadTable:[self.plansType integerValue]];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - Table view data source

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
#warning Potentially incomplete method implementation.
      return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
#warning Incomplete method implementation.
    return [_summaryArray count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSString *identifier = @"summaryCell";
    
    SummaryCell *cell = [tableView dequeueReusableCellWithIdentifier:identifier];
    if (!cell) {
        cell = [[SummaryCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:identifier];
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
        cell.textLabel.backgroundColor = [UIColor clearColor];
    }
    
    cell.summary = [_summaryArray objectAtIndex:indexPath.row];
    
    return cell;
}

/**
 * Tells client that refresh has been triggered
 * After reloading is completed must call [MNMBottomPullToRefreshManager tableViewReloadFinished]
 *
 * @param manager PTR manager
 */
- (void)bottomPullToRefreshTriggered:(MNMBottomPullToRefreshManager *)manager {
    
    [self performSelector:@selector(loadTable) withObject:nil afterDelay:1.0f];
}

/* */
-(void)loadTable:(int )plansType
{
    reloads_++;
    NSLog(@"start to loadtable ,reloads is:%d , plantype is:%d",reloads_,plansType );
    //请求参数
    NSDictionary *param = [NSDictionary dictionaryWithObjectsAndKeys:
                           [NSNumber numberWithFloat:plansType], @"type",
                           [NSNumber numberWithInt:reloads_ + 1], @"count",nil];
    
    [Summary getPlansSummaryWithBlock:^(NSArray *posts, NSError *error) {
        if (error) {
            [[[UIAlertView alloc] initWithTitle:NSLocalizedString(@"Error", nil) message:[error localizedDescription] delegate:nil cancelButtonTitle:nil otherButtonTitles:NSLocalizedString(@"OK", nil), nil] show];
            NSLog(@"Get plans summary error: %@", error);
        } else {
            // _posts = posts;
            if (posts != nil && [posts count] != 0) {
                //
                _summaryArray = [[NSMutableArray alloc] init];
                for (Summary *summary in posts) {
                    [_summaryArray addObject:summary];
                     NSLog(@"_posts -- %@ ", summary);
                }
                NSLog(@"_posts -- %@ ", _summaryArray);
            }
            [_table reloadData];
            [pullToRefreshManager_ tableViewReloadFinished];
        }
    }
                      requestParameter:param];
}


#pragma mark - Table view delegate

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    Summary *selectSummary = [_summaryArray objectAtIndex:indexPath.row];
    [self performSegueWithIdentifier:@"detailView" sender:selectSummary.destinationId ];
    
    NSLog(@"summary %@", selectSummary.destinationId);
}

#pragma mark segue
/*
 * Goto Next Page
 */
-(void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender{
    //进入到地图页面
    if([segue.identifier isEqualToString:@"mapView"]){
        //获取MapviewController
        MapViewController *view = [segue destinationViewController];
        if ([view respondsToSelector:@selector(setSummaryArray:)]) {
            [view setValue:_summaryArray forKey:@"summaryArray"];
        }
    }
    //进入到详细页面
    if ([segue.identifier isEqualToString:@"detailView"]) {
        DetailViewController *detailView = [segue destinationViewController];
        if ([detailView respondsToSelector:@selector(setDestinationId:)]) {
            [detailView setValue:sender forKey:@"destinationId"];
        }
    }
}

@end
