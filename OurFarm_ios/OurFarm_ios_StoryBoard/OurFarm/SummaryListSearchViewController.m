//
//  SummaryListSearchViewController.m
//  OurFarm
//
//  Created by tian hao on 13-6-18.
//  Copyright (c) 2013年 tian hao. All rights reserved.
//

#import "SummaryListSearchViewController.h"
#import "SummaryCell.h"
#import "MapViewController.h"
#import "DetailViewController.h"

@interface SummaryListSearchViewController ()

/** Loads the table @private*/
- (void)loadTable;

@end

@implementation SummaryListSearchViewController{
    @private NSString *_searchName;
    @private NSMutableArray *_summaryArray;
}

@synthesize table ;

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
    pullToRefreshManager_ = [[MNMBottomPullToRefreshManager alloc] initWithPullToRefreshViewHeight:60.0f tableView:table withClient:self];
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
 -(void)loadTable:(NSString *)searchName
{
    reloads_++;
    NSLog(@"start to loadtable ,reloads is:%d , searchName is:%@",reloads_,searchName );
    //请求参数
    NSDictionary *param = [NSDictionary dictionaryWithObjectsAndKeys:
                           [NSString stringWithString:searchName ], @"name",                           nil];

    [Summary getSearchSummaryWithBlock:^(NSArray *posts, NSError *error) {
        if (error) {
            [[[UIAlertView alloc] initWithTitle:NSLocalizedString(@"Error", nil) message:[error localizedDescription] delegate:nil cancelButtonTitle:nil otherButtonTitles:NSLocalizedString(@"OK", nil), nil] show];
            NSLog(@"Get search summary error: %@", error);
        } else {
            // _posts = posts;
            if (posts != nil && [posts count] != 0) {
                for (Summary *summary in posts) {
                    [_summaryArray addObject:summary];
                }
                NSLog(@"_posts -- %@ ", _summaryArray);
            }
            [table reloadData];
            [pullToRefreshManager_ tableViewReloadFinished];
        }
    }
                      requestParameter:param];
}



#pragma mark - Table view delegate

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    // Navigation logic may go here. Create and push another view controller.
    Summary *selectSummary = [_summaryArray objectAtIndex:indexPath.row];
    [self performSegueWithIdentifier:@"detailView" sender:selectSummary.destinationId ];
    
    NSLog(@"summary %@", selectSummary.destinationId);
}

#pragma mark searchBar delegate


//点击search 按钮
-(void)searchBarSearchButtonClicked:(UISearchBar *)searchBar {
    //关闭键盘
    [searchBar resignFirstResponder];
    if (_searchName == NULL) {
        //用户没有输入任何文字，检索默认地点
        _searchName = @"十渡";
    }
    NSLog(@"searchButtonClicked,searchName is:%@",_searchName);
    //清空list数据
    [_summaryArray removeAllObjects];
    [self loadTable:_searchName];
    //
    
}

//当textview的文字改变或者清除的时候调用此方法，
-(void) searchBar:(UISearchBar *)searchBar textDidChange:(NSString *)searchText{
    NSLog(@"textDidChanged:%@",searchText);
    _searchName = searchText;
}

/*
 * Goto MapView Page
 */
-(void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender{
    //传递参数
    
    if([segue.identifier isEqualToString:@"mapView"]){
        //获取MapviewController
        MapViewController *view = [segue destinationViewController];
        if ([view respondsToSelector:@selector(setSummaryArray:)]) {
            [view setValue:_summaryArray forKey:@"summaryArray"];
        }
    }
    
    if ([segue.identifier isEqualToString:@"detailView"]) {
        DetailViewController *detailView = [segue destinationViewController];
        [detailView setValue:sender forKey:@"destinationId"];
        NSLog(@"detail:%@", sender);
    }
}


@end
