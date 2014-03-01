//
//  ActiveListViewController.h
//  OurFarm
//
//  Created by tian hao on 13-6-14.
//  Copyright (c) 2013年 tian hao. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "MNMBottomPullToRefreshManager.h"

@interface ActiveListViewController : UITableViewController{
   @private
    /**
     * Reloads (for testing purposes)
     */
    NSUInteger reloads_;
    /**
     * Pull to refresh manager
     */
    MNMBottomPullToRefreshManager *pullToRefreshManager_;
}
@property (strong, nonatomic) IBOutlet UITableView *table;

@end
