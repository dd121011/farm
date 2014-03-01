//
//  SummaryListViewController.m
//  OurFarm
//
//  Created by tian hao on 13-6-7.
//  Copyright (c) 2013年 tian hao. All rights reserved.
//

#import "SummaryListViewController.h"
#import "SummaryCell.h"
#import "Summary.h"
#import "PostTableViewCell.h"
#import "MapViewController.h"
#import "DetailViewController.h"

#import "SVProgressHUD.h"

@interface SummaryListViewController ()

/**
 * Loads the table
 *
 * @private
 */
- (void)loadTable;

@end


@implementation SummaryListViewController {
    @private NSMutableArray *_summaryArray;
    __strong UIActivityIndicatorView *_activityIndicatorView;
}

@synthesize resJson;
@synthesize nearbyType;

@synthesize table = table_;

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
    [SVProgressHUD show];
    
    [super viewDidLoad];
    //获得参数
    NSLog(@"x=%d",[self.nearbyType integerValue]);
    
    reloads_ = -1;
    
    _summaryArray = [[NSMutableArray alloc] init];
    
    table_.rowHeight = 70.0f;
    
    //self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithCustomView:_activityIndicatorView];
    
    pullToRefreshManager_ = [[MNMBottomPullToRefreshManager alloc] initWithPullToRefreshViewHeight:60.0f tableView:table_ withClient:self];
    
    //start up get location
    locationManager = [[CLLocationManager alloc] init];
    [locationManager setDelegate:self];
    locationManager.distanceFilter = 1000;
    locationManager.desiredAccuracy = kCLLocationAccuracyKilometer;
    [locationManager startUpdatingLocation];
}

- (void)loadView {
    [super loadView];
    
    _activityIndicatorView = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleWhite];
    _activityIndicatorView.hidesWhenStopped = YES;
}

- (void)viewDidUnload {
    _activityIndicatorView = nil;
    
    [super viewDidUnload];
}

- (void)viewWillAppear:(BOOL)animated {
    
    [super viewWillAppear:animated];
    
    //[self loadTable];
}

#pragma mark 定位

/*
 * Get location error
 */
- (void)locationManager:(CLLocationManager *)manager didFailWithError:(NSError *)error
{
    NSLog(@"定位出错: %@", error);
}

/*
 * Get location success
 */
- (void)locationManager:(CLLocationManager *)manager didUpdateToLocation:(CLLocation *)newLocation fromLocation:(CLLocation *)oldLocation
{
    [manager stopUpdatingLocation];
    
    NSLog(@"%f,%f",newLocation.coordinate.latitude, newLocation.coordinate.longitude);
    coordinate.latitude = newLocation.coordinate.latitude;
    coordinate.longitude = newLocation.coordinate.longitude;
    
    [self loadTable:coordinate.latitude
                   :coordinate.longitude];
    
}

#pragma mark Aux view methods

/*
 * Loads the table
 */
- (void)loadTable:(float) lat
                 :(float) lng{
    reloads_++;
    
    NSLog(@"NearbyType: %d", [nearbyType intValue]);
    
    //[table_ reloadData];
    [_activityIndicatorView startAnimating];
    //请求参数
    NSDictionary *param = [NSDictionary dictionaryWithObjectsAndKeys:
                           [NSNumber numberWithFloat:lat], @"lat",
                           [NSNumber numberWithFloat:lng], @"lng",
                           [NSNumber numberWithInt:100], @"distance",
                           [NSNumber numberWithInt:reloads_ + 1], @"count",
                           [NSNumber numberWithInt: [self.nearbyType integerValue] ], @"type",
                           nil];
    
    [Summary getNearbySummaryWithBlock:^(NSArray *posts, NSError *error) {
        if (error) {
            [[[UIAlertView alloc] initWithTitle:NSLocalizedString(@"Error", nil) message:[error localizedDescription] delegate:nil cancelButtonTitle:nil otherButtonTitles:NSLocalizedString(@"OK", nil), nil] show];
            NSLog(@"Get nearby summary error: %@", error);
        } else {
            // _posts = posts;
            if (posts != nil && [posts count] != 0) {
                for (Summary *summary in posts) {
                    [_summaryArray addObject:summary];
                }
                NSLog(@"_posts -- %@ ", _summaryArray);
            }
            
            [SVProgressHUD dismiss];
            
            [table_ reloadData];
            [pullToRefreshManager_ tableViewReloadFinished];
            [_activityIndicatorView stopAnimating];
        }
    }
    requestParameter:param];
    
}

- (void)viewDidLayoutSubviews {
    
    [super viewDidLayoutSubviews];
    
    [pullToRefreshManager_ relocatePullToRefreshView];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - Table view data source

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    // Return the number of sections.
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    // Return the number of rows in the section.
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


#pragma mark - Table view delegate

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    // Navigation logic may go here. Create and push another view controller.
    Summary *selectSummary = [_summaryArray objectAtIndex:indexPath.row]; 
    [self performSegueWithIdentifier:@"detailView" sender:selectSummary.destinationId ];
    
    NSLog(@"summary %@", selectSummary.destinationId);
}

/**
 * Asks the delegate for the height to use for a row in a specified location.
 *
 * @param The table-view object requesting this information.
 * @param indexPath: An index path that locates a row in tableView.
 * @return A floating-point value that specifies the height (in points) that row should be.
 */
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    return tableView.rowHeight;
}

#pragma mark -
#pragma mark MNMBottomPullToRefreshManagerClient

/**
 * This is the same delegate method as UIScrollView but required in MNMBottomPullToRefreshManagerClient protocol
 * to warn about its implementation. Here you have to call [MNMBottomPullToRefreshManager tableViewScrolled]
 *
 * Tells the delegate when the user scrolls the content view within the receiver.
 *
 * @param scrollView: The scroll-view object in which the scrolling occurred.
 */
- (void)scrollViewDidScroll:(UIScrollView *)scrollView {
    [pullToRefreshManager_ tableViewScrolled];
}

/**
 * This is the same delegate method as UIScrollView but required in MNMBottomPullToRefreshClient protocol
 * to warn about its implementation. Here you have to call [MNMBottomPullToRefreshManager tableViewReleased]
 *
 * Tells the delegate when dragging ended in the scroll view.
 *
 * @param scrollView: The scroll-view object that finished scrolling the content view.
 * @param decelerate: YES if the scrolling movement will continue, but decelerate, after a touch-up gesture during a dragging operation.
 */
- (void)scrollViewDidEndDragging:(UIScrollView *)scrollView willDecelerate:(BOOL)decelerate {
    [pullToRefreshManager_ tableViewReleased];
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
        if ([detailView respondsToSelector:@selector(setNearbyType:)]) {
            [detailView setValue:self.nearbyType forKey:@"nearbyType"];
        }
    }
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)toInterfaceOrientation {
    return YES;
}

@end
