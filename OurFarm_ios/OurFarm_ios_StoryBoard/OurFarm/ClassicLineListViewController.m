//
//  ClassicLineListViewController.m
//  OurFarm
//
//  Created by tian hao on 13-6-20.
//  Copyright (c) 2013年 tian hao. All rights reserved.
//

#import "ClassicLineListViewController.h"
#import "SummaryCell.h"
#import "ClassicLineCell.h"
#import "ClassicLine.h"
#import "DetailClassicLineViewController.h"

@interface ClassicLineListViewController ()
    
@end

@implementation ClassicLineListViewController{
    @private NSMutableArray *_summaryArray;
}
@synthesize table;


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
    
    [self loadTable];
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
#warning Incomplete method implementation.
    // Return the number of rows in the section.
    return [_summaryArray count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSString *identifier = @"classicLineCell";
    
    ClassicLineCell *cell = [tableView dequeueReusableCellWithIdentifier:identifier];
    if (!cell) {
        cell = [[ClassicLineCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:identifier];
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
        cell.textLabel.backgroundColor = [UIColor clearColor];
    }
    //取出classicline
    ClassicLine *classicLine = [_summaryArray objectAtIndex:indexPath.row];
    //设置cell的属性
    [cell setClassicLine:classicLine];
    
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

#pragma mark - Table view delegate

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    // Navigation logic may go here. Create and push another view controller.
    ClassicLine *classicLine = [_summaryArray objectAtIndex:indexPath.row];
    [self performSegueWithIdentifier:@"detailClassicLineView" sender:classicLine ];
    
    NSLog(@"summary %@", classicLine);
}


/* */
-(void)loadTable
{
    reloads_++;
    NSLog(@"start to loadtable ,reloads is:%d ",reloads_ );
    //请求参数
    NSDictionary *param = [NSDictionary dictionaryWithObjectsAndKeys:
                           [NSNumber numberWithInt:reloads_ + 1], @"count", nil];
    
    [ClassicLine getClassicLineSummaryWithBlock:^(NSArray *posts, NSError *error) {
        if (error) {
            [[[UIAlertView alloc] initWithTitle:NSLocalizedString(@"Error", nil) message:[error localizedDescription] delegate:nil cancelButtonTitle:nil otherButtonTitles:NSLocalizedString(@"OK", nil), nil] show];
            NSLog(@"Get classicLine summary error: %@", error);
        } else {
            // _posts = posts;
            if (posts != nil && [posts count] != 0) {
                for (ClassicLine *summary in posts) {
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




/*
 * Goto MapView Page
 */
-(void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender{
    //传递参数
    
    if([segue.identifier isEqualToString:@"detailClassicLineView"]){
        //获取MapviewController
        DetailClassicLineViewController *view = [segue destinationViewController];
        if ([view respondsToSelector:@selector(setClassicLine:)]) {
            [view setValue:sender  forKey:@"classicLine"];
        }
    }
    
    }


@end
