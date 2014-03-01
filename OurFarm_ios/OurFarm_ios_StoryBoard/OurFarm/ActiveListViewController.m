//
//  ActiveListViewController.m
//  OurFarm
//
//  Created by tian hao on 13-6-14.
//  Copyright (c) 2013年 tian hao. All rights reserved.
//

#import "ActiveListViewController.h"
#import "ActiveCell.h"
#import "Activity.h"

@interface ActiveListViewController ()

@end

@implementation ActiveListViewController{
     @private NSMutableArray *_activityArray;
}

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
    _activityArray = [[NSMutableArray alloc] init];
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
    // Return the number of rows in the section.
    return [_activityArray count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *identifier = @"activeCell";
    
    ActiveCell *cell = [tableView dequeueReusableCellWithIdentifier:identifier];
    if (!cell) {
        cell = [[ActiveCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:identifier];
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
        cell.textLabel.backgroundColor = [UIColor clearColor];
    }
    
    cell.activity = [_activityArray objectAtIndex:indexPath.row];
    
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

/*
 * Loads the table
 */
- (void)loadTable{
    reloads_++;
    //请求参数
    NSDictionary *param = [NSDictionary dictionaryWithObjectsAndKeys:
                           [NSNumber numberWithInt:reloads_ + 1], @"count",nil];
    
    [Activity getActivityWithBlock:^(NSArray *posts, NSError *error) {
        if (error) {
            [[[UIAlertView alloc] initWithTitle:NSLocalizedString(@"Error", nil) message:[error localizedDescription] delegate:nil cancelButtonTitle:nil otherButtonTitles:NSLocalizedString(@"OK", nil), nil] show];
            NSLog(@"Get activity summary error: %@", error);
        } else {
            // _posts = posts;
            if (posts != nil && [posts count] != 0) {
                for (Activity *activity in posts) {
                    [_activityArray addObject:activity];
                }
                NSLog(@"_posts -- %@ ", _activityArray);
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
    // Navigation logic may go here. Create and push another view controller.
    /*
     <#DetailViewController#> *detailViewController = [[<#DetailViewController#> alloc] initWithNibName:@"<#Nib name#>" bundle:nil];
     // ...
     // Pass the selected object to the new view controller.
     [self.navigationController pushViewController:detailViewController animated:YES];
     */
}

@end
