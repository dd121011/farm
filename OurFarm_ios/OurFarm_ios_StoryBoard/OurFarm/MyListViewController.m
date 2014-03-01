//
//  MyListViewController.m
//  OurFarm
//
//  Created by 李 凤勇 on 13-6-26.
//  Copyright (c) 2013年 tian hao. All rights reserved.
//

#import "MyListViewController.h"
#import "Destination.h"
#import "Summary.h"
#import "SummaryCell.h"
#import "DetailViewController.h"
#import "FarmHomeDB.h"

@interface MyListViewController ()

/**
 * Loads the table
 *
 * @private
 */
- (void)loadTable;

@end

@implementation MyListViewController {
    @private NSMutableArray *_summaryArray;
}


-(IBAction)changeRightBarButton {
    if(self.editing) {
        // 取消列表行的可编辑状态
        self.tableView.editing = NO;
        self.tableView.allowsSelectionDuringEditing = NO;
        self.editing = NO;
        
        // 恢复按钮标题和样式
        self.navigationItem.rightBarButtonItem.title = @"编辑";
        self.navigationItem.rightBarButtonItem.style = UIBarButtonItemStyleBordered;
    } else {
        // 点击编辑按钮后，将列表的行设置为可编辑状态,红色圆形图标可以删除此记录
        self.tableView.editing = YES;
        self.tableView.allowsSelectionDuringEditing = YES;
        self.editing = YES;
        
        // 将编辑按钮标题改成“完成”，同时改变按钮样式
        self.navigationItem.rightBarButtonItem.title = @"完成";
        self.navigationItem.rightBarButtonItem.style = UIBarButtonItemStyleDone;
    }
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
	_summaryArray = [[NSMutableArray alloc] init];
    _table.rowHeight = 70.0f;
    
    if([_myType intValue] == 1) {
        self.title = @"我的收藏";
    } else if ([_myType intValue] == 2) {
        self.title = @"行程";
    }
    
    //add edit button
    self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithTitle:@"编辑"
                                                                              style:UIBarButtonItemStyleBordered
                                                                             target:self
                                                                             action:@selector(changeRightBarButton)];
    
    self.navigationItem.rightBarButtonItem.possibleTitles = [NSSet setWithObjects:@"编辑", @"完成", nil];
    
}

- (void)viewWillAppear:(BOOL)animated {
    
    [super viewWillAppear:animated];
    
    [_summaryArray removeAllObjects];
    [self loadTable];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

/**
 * Loads the table
 */
- (void)loadTable{

    NSLog(@"MyType: %d", [_myType intValue]);
    
    [Destination getMyListWithBlock:^(NSArray *summaryArray, NSError *error) {
        if (error) {
            [[[UIAlertView alloc] initWithTitle:NSLocalizedString(@"Error", nil) message:[error localizedDescription] delegate:nil cancelButtonTitle:nil otherButtonTitles:NSLocalizedString(@"OK", nil), nil] show];
            NSLog(@"Get myfavorite summary error: %@", error);
        } else {
            if (summaryArray != nil && [summaryArray count] != 0) {
                for (Summary *summary in summaryArray) {
                    [_summaryArray addObject:summary];
                }
                
            }
            [_table reloadData];
        }
    } myType:[_myType intValue]];
    
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
    
   // cell.summary = [_summaryArray objectAtIndex:indexPath.row];
    
    [cell setSummaryForMyFavorite:[_summaryArray objectAtIndex:indexPath.row]];
    
    return cell;
}

 // Override to support conditional editing of the table view.
 - (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath
 {
 // Return NO if you do not want the specified item to be editable.
 return YES;
 }
 


 // Override to support editing the table view.
 - (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath
 {
     if (editingStyle == UITableViewCellEditingStyleDelete) {
         Summary *summary = [_summaryArray objectAtIndex:[indexPath row]];
         
         if([_myType intValue] == 1) {//删除收藏数据
             [FarmHomeDB deleteFavorite: [summary.destinationId intValue]];
         } else if ([_myType intValue] == 2) {//删除本次行程数据
             [FarmHomeDB updateThisTripAfterDelete:[summary.destinationId intValue]];
             [FarmHomeDB deleteThisTrip: [summary.destinationId intValue]];
         }
         [_summaryArray removeObject:summary];
         
         // Delete the row from the data source.
         [tableView deleteRowsAtIndexPaths:[NSArray arrayWithObject:indexPath] withRowAnimation:UITableViewRowAnimationFade];
         
     } else if (editingStyle == UITableViewCellEditingStyleInsert) {
         // Create a new instance of the appropriate class, insert it into the array, and add a new row to the table view.
     }
     
 }


//移动cell时的操作
- (void)tableView:(UITableView *)tableView moveRowAtIndexPath:(NSIndexPath *)sourceIndexPath toIndexPath:(NSIndexPath *)destinationIndexPath{
    //获取移动的对象
    Summary *moveSummary = [_summaryArray objectAtIndex:sourceIndexPath.row];
    //因为在数据库中从1开始计算，而tableview中从0开始计算
    int from = sourceIndexPath.row + 1;
    int to = destinationIndexPath.row + 1;
    
    NSLog(@"move...%d..to..%d",from, to);
    
    if(from == to) return;
    //[FarmHomeDB updateThisTripSwapSort:from toSort:to];
    //向下移动
    if(sourceIndexPath.row < destinationIndexPath.row) {
        [FarmHomeDB updateThisTripBetweenSwap:from toSort:to moveWay:-1];
    } else if(sourceIndexPath.row > destinationIndexPath.row) {//向上移动
        [FarmHomeDB updateThisTripBetweenSwap:from toSort:to moveWay:1];
    }
    //更新移动对象的sort值
    [FarmHomeDB updateThisTrip:[moveSummary.destinationId intValue] atSort:to];
}

// Override to support conditional rearranging of the table view.
- (BOOL)tableView:(UITableView *)tableView canMoveRowAtIndexPath:(NSIndexPath *)indexPath
{
// Return NO if you do not want the item to be re-orderable.
    if([_myType intValue] == 1) {
        return NO;
    } else if ([_myType intValue] == 2) {
        return YES;
    }
}

#pragma mark - Table view delegate

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    // Navigation logic may go here. Create and push another view controller.
    Summary *selectSummary = [_summaryArray objectAtIndex:indexPath.row];
    [self performSegueWithIdentifier:@"detailView" sender:selectSummary.destinationId ];
    
    NSLog(@"summary %@", selectSummary.destinationId);
}

/*
 * Goto Next Page
 */
-(void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender{
    NSLog(@"Go to next page");
    //进入到详细页面
    if ([segue.identifier isEqualToString:@"detailView"]) {
        DetailViewController *detailView = [segue destinationViewController];
        [detailView setValue:sender forKey:@"destinationId"];
        NSLog(@"detail:%@", sender);
    }
    
}

@end
