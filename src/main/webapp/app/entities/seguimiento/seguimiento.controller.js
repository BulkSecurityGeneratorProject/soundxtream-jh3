(function() {
    'use strict';

    angular
        .module('soundxtream3App')
        .controller('SeguimientoController', SeguimientoController);

    SeguimientoController.$inject = ['Principal', 'User_friends', '$scope', '$state', 'Seguimiento', 'SeguimientoSearch', 'ParseLinks', 'AlertService', 'User', '$log'];

    function SeguimientoController (Principal, User_friends, $scope, $state, Seguimiento, SeguimientoSearch, ParseLinks, AlertService, User, $log) {
        var vm = this;

        vm.seguimientos = [];
        vm.loadPage = loadPage;
        vm.page = 0;
        vm.predicate = 'id';
        vm.reset = reset;
        vm.reverse = true;
        vm.clear = clear;
        vm.search = search;
        vm.users = [];
        vm.account = null;
        vm.friendNot = null;

        Principal.identity().then(function(account) {
            vm.account = account;
        });

        loadAll();

        function loadAll () {
            vm.friendNot = User_friends.friendshipNotifications();
            User.query({}, function (result, headers) {
                vm.links = ParseLinks.parse(headers('link'));
                vm.totalItems = headers('X-Total-Count');

                //hide anonymous user from user management: it's a required user for Spring Security
                for(var i in result) {
                    if(result[i]['login'] === 'anonymoususer') {
                        result.splice(i,1);
                    }
                }
                vm.users = result;
            });
            if (vm.currentSearch) {
                SeguimientoSearch.query({
                    query: vm.currentSearch,
                    page: vm.page,
                    size: 20,
                    sort: sort()
                }, onSuccess, onError);
            } else {
                Seguimiento.query({
                    page: vm.page,
                    size: 20,
                    sort: sort()
                }, onSuccess, onError);
            }
            function sort() {
                var result = [vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc')];
                if (vm.predicate !== 'id') {
                    result.push('id');
                }
                return result;
            }
            function onSuccess(data, headers) {
                vm.links = ParseLinks.parse(headers('link'));
                vm.totalItems = headers('X-Total-Count');
                for (var i = 0; i < data.length; i++) {
                    vm.seguimientos.push(data[i]);
                }
            }
            function onError(error) {
                AlertService.error(error.data.message);
            }
        }

        vm.confirmFriendship = function(user_friend) {
            user_friend.friend = true;
            User_friends.update(user_friend, function(res) {
                vm.friendNot = User_friends.friendshipNotifications();
            });
        };

        vm.addFriend = function(user) {
            $scope.friendShip = {
                id: null,
                friend: false,
                friend_from: null,
                friend_to: null,
                friendshipDate: null
            };
            $scope.friendShip.friend_to = user;
            User_friends.save($scope.friendShip, function(res) {
                $log.log(res);
            });
        };

        vm.follow = function(user) {
            $scope.seguimiento = {
                id: null,
                follower: null,
                followed: false,
                following: null,
                followingDate: null
            };
            console.log(user);
            $scope.seguimiento.followed = user;
            Seguimiento.save($scope.seguimiento,function(res) {
                $log.log(res);
                for(var k = 0; k < vm.seguimientos.length; k++){
                    if(vm.seguimientos[k].id == res.id){
                        vm.seguimientos[k].following = res.following;
                        vm.seguimientos[k].followingDate = res.followingDate;
                    }
                }
            });
        };

        function reset () {
            vm.page = 0;
            vm.seguimientos = [];
            loadAll();
        }

        function loadPage(page) {
            vm.page = page;
            loadAll();
        }

        function clear () {
            vm.seguimientos = [];
            vm.links = null;
            vm.page = 0;
            vm.predicate = 'id';
            vm.reverse = true;
            vm.searchQuery = null;
            vm.currentSearch = null;
            vm.loadAll();
        }

        function search (searchQuery) {
            if (!searchQuery){
                return vm.clear();
            }
            vm.seguimientos = [];
            vm.links = null;
            vm.page = 0;
            vm.predicate = '_score';
            vm.reverse = false;
            vm.currentSearch = searchQuery;
            vm.loadAll();
        }
    }
})();
